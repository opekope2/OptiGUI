package opekope2.optigui.internal.resource.loader

import com.google.common.collect.LinkedListMultimap
import com.google.common.collect.Multimap
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.Resource
import net.minecraft.server.packs.resources.ResourceManager
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
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import kotlin.jvm.optionals.getOrNull

internal abstract class AbstractResourceLoader<TResource>(id: ResourceLocation) : PreparableReloadListener,
    IFilterLoader {
    constructor(id: String) : this(ResourceLocation.fromNamespaceAndPath(MOD_ID, id))

    init {
        IFilterLoader.register(id, this)
    }

    protected val logger: EventCollectorLogger = EventCollectorLogger(LoggerFactory.getLogger(javaClass))

    final override val log: List<LoggingEvent> get() = logger.events

    final override lateinit var filters: Multimap<InteractionTarget, TextureChangerFilter>

    protected abstract fun findResources(manager: ResourceManager): Map<ResourceLocation, Resource>

    protected abstract fun loadResource(
        resource: IdentifiableResource<Resource>,
        manager: ResourceManager,
        collector: ResourceCollector,
    )

    private fun createLoadTimeNbt() = ILoadTimeNbtProvider.fold(CompoundTag()) { compound, (key, supplier) ->
        compound.also { it.put(key, supplier.get()) }
    }

    private fun logError(resource: IdentifiableResource<*>, e: Throwable) {
        logger.atError()
            .setCause(e)
            .addKeyValue(LOG_KEY_RESOURCE_PACK, resource.packId)
            .addKeyValue(LOG_KEY_RESOURCE, resource.id)
            .log("{}", e.message)
    }

    private fun loadResources(
        resources: Map<ResourceLocation, Resource>,
        resourceManager: ResourceManager,
        collector: ResourceCollector,
        executor: Executor
    ): CompletableFuture<*> {
        val tasks = resources.mapTo(ArrayList(resources.size)) { (resourceId, resource) ->
            val resource = IdentifiableResource(resource.sourcePackId(), resourceId, resource)
            CompletableFuture.supplyAsync({ loadResource(resource, resourceManager, collector) }, executor)
                .exceptionallyAsync({ logError(resource, it) }, executor)
                .thenRunAsync({ resource.resource }, executor)
        }

        return CompletableFuture.allOf(*tasks.toTypedArray())
    }

    override fun reload(
        preparationBarrier: PreparableReloadListener.PreparationBarrier,
        resourceManager: ResourceManager,
        preparationsProfiler: ProfilerFiller,
        reloadProfiler: ProfilerFiller,
        backgroundExecutor: Executor,
        gameExecutor: Executor
    ): CompletableFuture<Void?> {
        logger.events.clear()

        val resources = CompletableFuture.supplyAsync({ findResources(resourceManager) }, backgroundExecutor)
        val collector = CompletableFuture.supplyAsync(::createLoadTimeNbt, backgroundExecutor)
            .thenApply { ResourceCollector(logger, it) }

        return resources
            .thenCombine(collector) { resources, collector -> resources to collector }
            .thenComposeAsync({ (resources, collector) ->
                loadResources(resources, resourceManager, collector, backgroundExecutor)
            }, backgroundExecutor)
            .thenCombine(collector) { _, collector -> collector }
            .thenCompose(preparationBarrier::wait)
            .thenAcceptAsync({ loadFilters(it, resourceManager) }, gameExecutor)
    }

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

    private fun loadFilters(collector: ResourceCollector, manager: ResourceManager) {
        val guiAtlasManager = mc.guiSprites
        val missingSprite = guiAtlasManager.getSprite(MissingTextureAtlasSprite.getLocation())

        filters = LinkedListMultimap.create()
        for (resource in collector) {
            val json = resource.resource
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
