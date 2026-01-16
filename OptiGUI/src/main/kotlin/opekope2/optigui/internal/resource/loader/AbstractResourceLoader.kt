package opekope2.optigui.internal.resource.loader

import com.google.common.collect.LinkedListMultimap
import com.google.common.collect.Multimap
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.Resource
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import opekope2.optigui.filter.IFilterLoader
import opekope2.optigui.filter.texture_changer.TextureChangerFilter
import opekope2.optigui.interaction.InteractionTarget
import opekope2.optigui.internal.I18n
import opekope2.optigui.nbt_provider.ILoadTimeNbtProvider
import opekope2.optigui.resource.format.json.JsonTextureChanger
import opekope2.optigui.util.*
import org.slf4j.LoggerFactory
import org.slf4j.event.LoggingEvent
import kotlin.jvm.optionals.getOrNull

private typealias Resources<T> = List<IdentifiableResource<T>>

internal abstract class AbstractResourceLoader<TResource>(val id: ResourceLocation) :
    SimplePreparableReloadListener<Resources<TResource>>(), IFilterLoader {
    constructor(id: String) : this(ResourceLocation.fromNamespaceAndPath(MOD_ID, id))

    init {
        IFilterLoader.register(id, this)
    }

    private lateinit var loadTimeNbt: CompoundTag

    protected val logger: EventCollectorLogger = EventCollectorLogger(LoggerFactory.getLogger(javaClass))

    final override val log: List<LoggingEvent> get() = logger.events

    final override lateinit var filters: Multimap<InteractionTarget, TextureChangerFilter>

    protected abstract fun findResources(manager: ResourceManager): Map<ResourceLocation, Resource>

    final override fun prepare(manager: ResourceManager, profiler: ProfilerFiller): Resources<TResource> {
        logger.events.clear()
        loadTimeNbt = CompoundTag()
        for ((key, supplier) in ILoadTimeNbtProvider.Registry) loadTimeNbt.put(key, supplier.get())

        return buildList {
            for ((resourceId, resource) in findResources(manager)) {
                try {
                    logger.atDebug()
                        .addKeyValue(LOG_KEY_RESOURCE_PACK, resource.sourcePackId())
                        .addKeyValue(LOG_KEY_RESOURCE, resourceId)
                        .addArgument(I18n.OPTIGUI_RP_LOADER_INFO_LOADING_RESOURCE.supplyTranslation())
                        .addArgument(resourceId)
                        .log("{} {}")
                    val loadedResource = loadResource(resource.sourcePackId(), resourceId, resource, manager)
                    add(IdentifiableResource(resource.sourcePackId(), resourceId, loadedResource))
                } catch (e: Exception) {
                    logger.atError()
                        .setCause(e)
                        .addKeyValue(LOG_KEY_RESOURCE_PACK, resource.sourcePackId())
                        .addKeyValue(LOG_KEY_RESOURCE, resourceId)
                        .log("{}", e.message)
                }
            }
        }
    }

    protected abstract fun loadResource(
        packId: String,
        resourceId: ResourceLocation,
        resource: Resource,
        manager: ResourceManager
    ): TResource

    protected abstract fun parseResource(resource: IdentifiableResource<TResource>, collector: ResourceCollector)

    private inline fun createTextureChangers(
        resource: IdentifiableResource<*>,
        jsonTextureChangers: Map<ResourceLocation, JsonTextureChanger>,
        message: I18n,
        crossinline textureValidator: (ResourceLocation) -> Boolean
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

    final override fun apply(prepared: Resources<TResource>, manager: ResourceManager, profiler: ProfilerFiller) {
        val guiAtlasManager = mc.guiSprites
        val missingSprite = guiAtlasManager.getSprite(MissingTextureAtlasSprite.getLocation())
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
            val json = resource.resource
            if (json.blocks.isEmpty() && json.entities.isEmpty() && json.items.isEmpty() && !json.inventory && !json.unknown) {
                logger.atWarn()
                    .addKeyValue(LOG_KEY_RESOURCE_PACK, resource.packId)
                    .addKeyValue(LOG_KEY_RESOURCE, resource.id)
                    .addArgument(I18n.OPTIGUI_RP_LOADER_WARN_NO_INTERACTION_TARGET.supplyTranslation())
                    .log("{}")
                continue
            }

            val textureChangers = createTextureChangers(
                resource,
                json.textureChangers,
                I18n.OPTIGUI_RP_LOADER_WARN_MISSING_TEXTURES
            ) { manager.getResource(it).isPresent }
            val spriteChangers = createTextureChangers(
                resource,
                json.spriteChangers,
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
                json.filter,
                textureChangers,
                spriteChangers,
                ArrayList(json.textStyleChangers),
            )

            for (block in json.blocks) filters[InteractionTarget.Block(block)] += filter
            for (entity in json.entities) filters[InteractionTarget.Entity(entity)] += filter
            for (item in json.items) filters[InteractionTarget.Item(item)] += filter
            if (json.inventory) filters[InteractionTarget.Inventory] += filter
            if (json.unknown) filters[InteractionTarget.Unknown] += filter
        }
    }
}
