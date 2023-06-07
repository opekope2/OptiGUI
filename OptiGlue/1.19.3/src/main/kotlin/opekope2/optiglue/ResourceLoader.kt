package opekope2.optiglue

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceReloader
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler
import opekope2.optiglue.mc_1_19_3.GlueResource
import opekope2.optigui.internal.service.ResourceLoaderService
import opekope2.optigui.resource.OptiFineConvertedResource
import opekope2.optigui.resource.OptiGuiResource
import opekope2.optigui.service.getService
import opekope2.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

internal object ResourceLoader : IdentifiableResourceReloadListener {
    private val resourceLoader = getService<ResourceLoaderService>()

    override fun getFabricId() = Identifier("optiglue", "optigui_resource_loader")

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

        return applyStart.thenAcceptAsync(resourceLoader::loadResources, applyExecutor)
    }

    private fun findOptiGuiResources(manager: ResourceManager) =
        manager.findResources(OPTIGUI_RESOURCES_ROOT) { (ns, path) ->
            ns == OPTIGUI_NAMESPACE && path.endsWith(".ini")
        }.map { it.key }

    private fun findOptiFineResources(manager: ResourceManager) =
        manager.findResources(OPTIFINE_RESOURCES_ROOT) { (ns, path) ->
            ns == Identifier.DEFAULT_NAMESPACE && path.endsWith(".properties")
        }.map { it.key }
}
