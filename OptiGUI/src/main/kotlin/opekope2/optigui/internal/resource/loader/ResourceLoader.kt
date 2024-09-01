package opekope2.optigui.internal.resource.loader

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler
import opekope2.optigui.internal.TextureReplacer
import opekope2.optigui.internal.filter.TextureReplacerFilter
import opekope2.optigui.internal.util.OrderedListLruAccessor
import opekope2.optigui.resource.loader.IResourceLoader
import opekope2.optigui.resource.loader.ResourceLoaders
import opekope2.optigui.util.MOD_ID
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.Consumer

private typealias ResourceLoaderData = Map<Identifier, ResourceLoader.Data<Any>>

internal object ResourceLoader : SimpleResourceReloadListener<ResourceLoaderData>, ClientModInitializer {
    private val LOGGER = LoggerFactory.getLogger("OptiGUI/ResourceLoader")

    override fun getFabricId(): Identifier = Identifier.of(MOD_ID, "resource_loader")

    override fun load(
        manager: ResourceManager,
        profiler: Profiler,
        executor: Executor
    ): CompletableFuture<ResourceLoaderData> = CompletableFuture.supplyAsync({ loadResources(manager) }, executor)

    override fun apply(
        data: ResourceLoaderData,
        manager: ResourceManager,
        profiler: Profiler,
        executor: Executor
    ): CompletableFuture<Void> = CompletableFuture.runAsync({ processResources(manager, data) }, executor)

    override fun onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ResourceLoader)
    }

    @Suppress("UNCHECKED_CAST")
    private fun loadResources(manager: ResourceManager): ResourceLoaderData {
        val data = mutableMapOf<Identifier, Data<Any>>()

        for ((_, loader) in ResourceLoaders as Iterable<Map.Entry<*, IResourceLoader<Any>>>) {
            val resources = manager.findResources(loader.startingPath, loader::canLoad)
            for ((id, resource) in resources) {
                if (id in data) {
                    LOGGER.warn("Ignoring duplicate resource `{}`", id)
                    continue
                }

                loader.loadResource(id, resource, LOGGER)?.let {
                    data[id] = Data(loader, id, it)
                }
            }
        }

        return data
    }

    private fun processResources(manager: ResourceManager, resourceLoaderData: ResourceLoaderData) {
        val loadedResourceGetter = { id: Identifier -> resourceLoaderData[id]?.loadedResource }
        val replaceableTextures = mutableSetOf<Identifier>()
        val replaceableTextureAdder = Consumer(replaceableTextures::add)
        val containerFilters = mutableMapOf<Identifier?, MutableList<TextureReplacerFilter>>()
        val containerFilterAdder = { container: Identifier, filter: TextureReplacerFilter ->
            containerFilters.getOrPut(container, ::mutableListOf) += filter
        }

        resourceLoaderData.forEach { (_, data) ->
            val ctx = ResourceLoadingContext(
                manager,
                loadedResourceGetter,
                replaceableTextureAdder,
                containerFilterAdder,
                data.resourceId,
                data.loadedResource,
                LOGGER
            )
            data.resourceLoader.processResource(ctx)
        }

        TextureReplacer.loadFilters(
            ImmutableMap.copyOf(containerFilters.mapValues { (_, list) -> OrderedListLruAccessor(list) }),
            ImmutableSet.copyOf(replaceableTextures)
        )
    }

    internal data class Data<T>(
        val resourceLoader: IResourceLoader<T>,
        val resourceId: Identifier,
        val loadedResource: T
    )
}
