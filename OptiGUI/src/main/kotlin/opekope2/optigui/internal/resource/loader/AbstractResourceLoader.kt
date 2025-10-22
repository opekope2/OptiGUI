package opekope2.optigui.internal.resource.loader

import com.google.common.collect.LinkedListMultimap
import com.google.common.collect.Multimap
import net.minecraft.client.MinecraftClient
import net.minecraft.client.texture.MissingSprite
import net.minecraft.nbt.NbtCompound
import net.minecraft.resource.Resource
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.SinglePreparationResourceReloader
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler
import opekope2.optigui.filter.IFilterLoader
import opekope2.optigui.filter.texture_changer.TextureChangerFilter
import opekope2.optigui.interaction.InteractionTarget
import opekope2.optigui.internal.I18n
import opekope2.optigui.nbt_provider.ILoadTimeNbtProvider
import opekope2.optigui.resource.format.json.JsonTextureChanger
import opekope2.optigui.util.*
import opekope2.optigui.util.collections.LinkedMruCollection
import org.slf4j.LoggerFactory
import kotlin.jvm.optionals.getOrNull

private typealias Resources<T> = List<IdentifiableResource<T>>

internal abstract class AbstractResourceLoader<TResource>(val id: Identifier) :
    SinglePreparationResourceReloader<Resources<TResource>>(), IFilterLoader {
    init {
        IFilterLoader.register(id, this)
    }

    private lateinit var loadTimeNbt: NbtCompound

    protected val logger: EventCollectorLogger = EventCollectorLogger(LoggerFactory.getLogger(javaClass))

    final override val errors: List<ResourceLoadingLoggingEvent>
        get() = logger.events.map {
            ResourceLoadingLoggingEvent.fromLoggingEvent(
                it,
                MinecraftClient.getInstance().resourcePackManager::hasProfile
            )
        }

    final override lateinit var filters: Multimap<InteractionTarget, TextureChangerFilter>

    protected abstract fun findResources(manager: ResourceManager): Map<Identifier, Resource>

    final override fun prepare(manager: ResourceManager, profiler: Profiler): Resources<TResource> {
        logger.events.clear()
        loadTimeNbt = NbtCompound()
        for ((key, supplier) in ILoadTimeNbtProvider.Registry) loadTimeNbt.put(key, supplier.get())

        return buildList {
            for ((resourceId, resource) in findResources(manager)) {
                try {
                    logger.atDebug()
                        .addKeyValue(LOG_KEY_RESOURCE_PACK, resource.packId)
                        .addKeyValue(LOG_KEY_RESOURCE, resourceId)
                        .addArgument(I18n.OPTIGUI_RP_LOADER_INFO_LOADING_RESOURCE.supplyTranslation())
                        .addArgument(resourceId)
                        .log("{} {}")
                    val loadedResource = loadResource(resource.packId, resourceId, resource, manager)
                    add(IdentifiableResource(resource.packId, resourceId, loadedResource))
                } catch (e: Exception) {
                    logger.atError()
                        .setCause(e)
                        .addKeyValue(LOG_KEY_RESOURCE_PACK, resource.packId)
                        .addKeyValue(LOG_KEY_RESOURCE, resourceId)
                        .log("{}", e.message)
                }
            }
        }
    }

    protected abstract fun loadResource(
        packId: String,
        resourceId: Identifier,
        resource: Resource,
        manager: ResourceManager
    ): TResource

    protected abstract fun parseResource(resource: IdentifiableResource<TResource>, collector: ResourceCollector)

    private inline fun createTextureChangers(
        resource: IdentifiableResource<*>,
        jsonTextureChangers: Map<Identifier, JsonTextureChanger>,
        message: I18n,
        crossinline textureValidator: (Identifier) -> Boolean
    ) = buildMap {
        val missing = mutableSetOf<String>()

        for ((original, changer) in jsonTextureChangers) {
            val result = changer.createTextureChanger { id ->
                RelativeIdentifier.toIdentifier(id, resource.id).takeIf(textureValidator)
                    .also { if (it == null) missing += RelativeIdentifier.toString(id) }
            }.resultOrPartial()

            // Not using Optional.ifPresent(Consumer) so everything gets properly inlined
            val textureChanger = result.getOrNull() ?: continue
            put(original, textureChanger)
        }

        if (missing.isNotEmpty()) logger.atWarn()
            .addKeyValue(LOG_KEY_RESOURCE_PACK, resource.packId)
            .addKeyValue(LOG_KEY_RESOURCE, resource.id)
            .addArgument { message.getTranslation(missing.joinToString()) }
            .log("{}")
    }

    final override fun apply(prepared: Resources<TResource>, manager: ResourceManager, profiler: Profiler) {
        val guiAtlasManager = MinecraftClient.getInstance().guiAtlasManager
        val missingSprite = guiAtlasManager.getSprite(MissingSprite.getMissingSpriteId())
        val resourceCollector = ResourceCollector(logger, loadTimeNbt)

        for (resource in prepared) {
            val (packId, resourceId) = resource
            try {
                parseResource(resource, resourceCollector)
                logger.atDebug()
                    .addKeyValue(LOG_KEY_RESOURCE_PACK, packId)
                    .addKeyValue(LOG_KEY_RESOURCE, resourceId)
                    .addArgument(I18n.OPTIGUI_RP_LOADER_INFO_LOAD_SUCCESS.supplyTranslation())
                    .addArgument(resourceId)
                    .log("{} {}")
            } catch (e: Exception) {
                logger.atError()
                    .setCause(e)
                    .addKeyValue(LOG_KEY_RESOURCE_PACK, packId)
                    .addKeyValue(LOG_KEY_RESOURCE, resourceId)
                    .log("{}", e.message)
                continue
            }
        }

        filters = LinkedListMultimap.create()
        for (resource in resourceCollector) {
            val jsonV2 = resource.resource
            if (jsonV2.blocks.isEmpty() && jsonV2.entities.isEmpty() && jsonV2.items.isEmpty() && !jsonV2.inventory && !jsonV2.unknown) {
                logger.atWarn()
                    .addKeyValue(LOG_KEY_RESOURCE_PACK, resource.packId)
                    .addKeyValue(LOG_KEY_RESOURCE, resource.id)
                    .addArgument(I18n.OPTIGUI_RP_LOADER_WARN_NO_INTERACTION_TARGET.supplyTranslation())
                    .log("{}")
                continue
            }

            val textureChangers = createTextureChangers(
                resource,
                jsonV2.textureChangers,
                I18n.OPTIGUI_RP_LOADER_WARN_MISSING_TEXTURES
            ) { manager.getResource(it).isPresent }
            val spriteChangers = createTextureChangers(
                resource,
                jsonV2.spriteChangers,
                I18n.OPTIGUI_RP_LOADER_WARN_MISSING_SPRITES
            ) { guiAtlasManager.getSprite(it) !== missingSprite }

            if (textureChangers.isEmpty() && spriteChangers.isEmpty()) {
                logger.atWarn()
                    .addKeyValue(LOG_KEY_RESOURCE_PACK, resource.packId)
                    .addKeyValue(LOG_KEY_RESOURCE, resource.id)
                    .addArgument(I18n.OPTIGUI_RP_LOADER_WARN_NO_TEXTURES_OR_SPRITES.supplyTranslation())
                    .log("{}")
                continue
            }

            val filter = TextureChangerFilter(
                resource.id,
                jsonV2.filter,
                textureChangers,
                spriteChangers,
                LinkedMruCollection(jsonV2.textStyleChangers)
            )

            for (block in jsonV2.blocks) filters[InteractionTarget.Block(block)] += filter
            for (entity in jsonV2.entities) filters[InteractionTarget.Entity(entity)] += filter
            for (item in jsonV2.items) filters[InteractionTarget.Item(item)] += filter
            if (jsonV2.inventory) filters[InteractionTarget.Inventory] += filter
            if (jsonV2.unknown) filters[InteractionTarget.Unknown] += filter
        }
    }
}
