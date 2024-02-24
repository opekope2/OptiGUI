package opekope2.optigui.internal

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.client.MinecraftClient
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceReloader
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler
import opekope2.optigui.exception.ResourceNotFoundException
import opekope2.optigui.filter.FirstMatchFilter
import opekope2.optigui.filter.IFilter
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.filter.IdentifiableFilter
import opekope2.optigui.internal.filter.factory.ReusableFilterFactoryContext
import opekope2.optigui.internal.interaction.FilterFactoryStore.filterFactories
import opekope2.optigui.resource.OptiFineConvertedResource
import opekope2.optigui.resource.OptiGuiResource
import opekope2.optigui.resource.ResourceReader
import opekope2.util.*
import java.io.InputStream
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import kotlin.jvm.optionals.getOrNull

internal object ResourceLoader : IdentifiableResourceReloadListener {
    override fun getFabricId() = Identifier("optigui", "optigui_resource_loader")

    override fun reload(
        synchronizer: ResourceReloader.Synchronizer,
        manager: ResourceManager,
        prepareProfiler: Profiler,
        applyProfiler: Profiler,
        prepareExecutor: Executor,
        applyExecutor: Executor
    ): CompletableFuture<Void> {
        val findOptiGui = CompletableFuture
            .supplyAsync({ findOptiGuiResources(manager) }, prepareExecutor)
            .thenApplyAsync({ it.map(::GlueResource) }, prepareExecutor)
            .thenApplyAsync({ it.map { res -> res.use(::OptiGuiResource) } }, prepareExecutor)
        val findOptiFine = CompletableFuture
            .supplyAsync({ findOptiFineResources(manager) }, prepareExecutor)
            .thenApplyAsync({ it.map(::GlueResource) }, prepareExecutor)
            .thenApplyAsync({ it.map { res -> res.use(::OptiFineConvertedResource) } }, prepareExecutor)

        val findAll = findOptiGui.thenCombineAsync(
            findOptiFine,
            { optiGuiResources, optiFineResources -> optiGuiResources union optiFineResources },
            prepareExecutor
        )

        val applyStart = findAll.thenCompose(synchronizer::whenPrepared)

        return applyStart.thenAcceptAsync(::loadResources, applyExecutor)
    }

    private fun findOptiGuiResources(manager: ResourceManager) =
        manager.findResources(OPTIGUI_RESOURCES_ROOT) { (ns, path) ->
            ns == OPTIGUI_NAMESPACE && path.endsWith(".ini")
        }.map { it.key }.toSet()

    private fun findOptiFineResources(manager: ResourceManager) =
        manager.findResources(OPTIFINE_RESOURCES_ROOT) { (ns, path) ->
            ns == Identifier.DEFAULT_NAMESPACE && path.endsWith(".properties")
        }.map { it.key }.toSet()

    private fun loadResources(resources: Set<OptiGuiResource>) {
        val filters = mutableListOf<IFilter<Interaction, Identifier>>()
        val replaceableTextures = mutableSetOf<Identifier>()

        val context = ReusableFilterFactoryContext()
        for (resource in resources) {
            logger.info("Loading ${resource.id} from ${resource.resourcePack}")

            context.resource = resource

            for ((modId, factories) in filterFactories) {
                context.modId = modId

                for (factory in factories) {
                    val (filter, replaceable) = try {
                        factory.createFilter(context)
                    } catch (exception: Exception) {
                        logger.warn("$modId threw an exception while creating filter for ${resource.id}.", exception)
                        continue
                    } ?: continue

                    filters.add(IdentifiableFilter(modId, filter, replaceable, resource))
                    replaceableTextures.addAll(replaceable)
                }
            }
        }

        val filter = FirstMatchFilter(filters)

        TextureReplacer.filter = filter
        TextureReplacer.replaceableTextures = replaceableTextures

        logger.debug("Filter chain loaded on resource reload:\n${filter.dump()}")
    }

    private class GlueResource(id: Identifier) : ResourceReader(id) {
        private val resource = manager.getResource(id).getOrNull()

        override fun exists(): Boolean = resource != null
        override val resourcePack: String
            get() = resource?.resourcePackName ?: throw ResourceNotFoundException(id)
        override val inputStream: InputStream
            get() = resource?.inputStream ?: throw ResourceNotFoundException(id)

        override fun close() {
            resource?.inputStream?.close()
        }

        companion object {
            private val manager: ResourceManager by lazy { MinecraftClient.getInstance().resourceManager }
        }
    }
}
