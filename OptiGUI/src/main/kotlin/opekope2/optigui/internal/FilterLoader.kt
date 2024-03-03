package opekope2.optigui.internal

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceReloader
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler
import opekope2.optigui.filter.ConjunctionFilter
import opekope2.optigui.filter.IFilter
import opekope2.optigui.filter.PostProcessorFilter
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.filter.ContainerMapFilter
import opekope2.optigui.internal.filter.ContainerMapFirstMatchFilter
import opekope2.optigui.internal.util.eventBuilder
import opekope2.optigui.registry.FilterLoaderRegistry
import opekope2.optigui.registry.LoadTimeSelectorRegistry
import opekope2.optigui.registry.SelectorRegistry
import opekope2.optigui.resource.IRawFilterData
import opekope2.optigui.util.MOD_ID
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

@Suppress("unused")
internal object FilterLoader : IdentifiableResourceReloadListener, ClientModInitializer {
    private val LOGGER = LoggerFactory.getLogger("OptiGUI/FilterLoader")

    override fun getFabricId() = Identifier(MOD_ID, "filter_loader")

    override fun reload(
        synchronizer: ResourceReloader.Synchronizer,
        manager: ResourceManager,
        prepareProfiler: Profiler,
        applyProfiler: Profiler,
        prepareExecutor: Executor,
        applyExecutor: Executor
    ): CompletableFuture<Void> {
        return CompletableFuture.supplyAsync({ loadRawFilters(manager, LOGGER) }, prepareExecutor)
            .thenCompose(synchronizer::whenPrepared)
            .thenAcceptAsync({ loadFilters(manager, it, LOGGER) }, applyExecutor)
    }

    override fun onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(FilterLoader)
    }

    private fun loadRawFilters(manager: ResourceManager, logger: Logger) =
        FilterLoaderRegistry.flatMap { resourceLoader ->
            resourceLoader.loadRawFilters(manager, logger)
        }

    private fun loadFilters(manager: ResourceManager, data: Collection<IRawFilterData>, logger: Logger) {
        val filters = data.mapNotNull { rawFilterData ->
            when {
                loadTimeSelectorsSkip(rawFilterData, logger) -> null

                rawFilterData.replaceableTextures.isEmpty() -> {
                    logger.eventBuilder(WARN, rawFilterData.resource, rawFilterData.container).log(
                        "Ignoring container `{}` in `{}`, because no replaceable textures were specified",
                        rawFilterData.container, rawFilterData.resource
                    )
                    null
                }

                manager.getResource(rawFilterData.replacementTexture).isEmpty -> {
                    logger.eventBuilder(WARN, rawFilterData.resource, rawFilterData.container).log(
                        "Ignoring container `{}` in `{}`, because replacement texture `{}` is missing",
                        rawFilterData.container, rawFilterData.resource, rawFilterData.replacementTexture
                    )
                    null
                }

                else -> {
                    val selectorFilters = createFilters(rawFilterData, logger)

                    rawFilterData to PostProcessorFilter(
                        ConjunctionFilter(selectorFilters),
                        rawFilterData.replacementTexture
                    )
                }
            }
        }

        val replaceable = filters.flatMapTo(mutableSetOf()) { (raw) -> raw.replaceableTextures }

        val groupedFilters = filters
            .groupBy { (raw) -> raw.container }
            .mapValues { (container, rawFilters) ->
                ContainerMapFirstMatchFilter(
                    container,
                    rawFilters
                        .sortedByDescending { (raw) -> raw.priority }
                        .map { (_, filter) -> filter }
                )
            }

        TextureReplacer.onFiltersLoaded(
            // Accelerate evaluation by only evaluating the matching container's filters, and not others
            ContainerMapFilter(groupedFilters),
            replaceable
        )
    }

    private fun loadTimeSelectorsSkip(rawFilterData: IRawFilterData, logger: Logger): Boolean {
        for ((selectorKey, selectorValue) in rawFilterData.rawSelectorData) {
            if (selectorKey !in LoadTimeSelectorRegistry) continue

            try {
                val result = LoadTimeSelectorRegistry[selectorKey]!!.evaluate(selectorValue)
                if (result !is IFilter.Result.Match) {
                    logger.eventBuilder(INFO, rawFilterData.resource, rawFilterData.container).log(
                        "Ignoring container `{}` in `{}`, because load-time selector `{}={}` resulted in `{}`",
                        rawFilterData.container, rawFilterData.resource, selectorKey, selectorValue, result
                    )
                    return true
                }
            } catch (e: Exception) {
                logger.eventBuilder(ERROR, rawFilterData.resource, rawFilterData.container)
                    .setCause(e)
                    .log(
                        "Ignoring container `{}` in `{}`, because load-time selector `{}={}` threw an exception",
                        rawFilterData.container, rawFilterData.resource, selectorKey, selectorValue
                    )
                return true
            }
        }
        return false
    }

    private fun createFilters(rawFilterData: IRawFilterData, logger: Logger): Collection<IFilter<Interaction, *>> {
        val selectorFilters = mutableListOf<IFilter<Interaction, *>>()
        // Filtering Interaction.container is redundant as ContainerMapFilter does this

        for ((selectorKey, selectorValue) in rawFilterData.rawSelectorData) {
            if (selectorKey !in SelectorRegistry) {
                if (selectorKey !in LoadTimeSelectorRegistry) {
                    logger.eventBuilder(WARN, rawFilterData.resource, rawFilterData.container).log(
                        "Unknown selector `{}` in container `{}` in `{}`",
                        selectorKey, rawFilterData.container, rawFilterData.resource
                    )
                }
                continue
            }

            try {
                SelectorRegistry[selectorKey]!!.createFilter(selectorValue)?.also(selectorFilters::add)
                    ?: logger.eventBuilder(INFO, rawFilterData.resource, rawFilterData.container).log(
                        "No filter was created for container `{}` in `{}`",
                        rawFilterData.container, rawFilterData.resource
                    )
            } catch (e: Exception) {
                logger.eventBuilder(ERROR, rawFilterData.resource, rawFilterData.container)
                    .setCause(e)
                    .log(
                        "No filter was created for container `{}` in `{}`, because selector `{}={}` threw an exception",
                        rawFilterData.container, rawFilterData.resource, selectorKey, selectorValue
                    )
            }
        }
        return selectorFilters
    }
}
