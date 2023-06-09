package opekope2.optiglue_1_18

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceReloader
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler
import opekope2.optigui.internal.service.ResourceLoaderService
import opekope2.optigui.resource.OptiFineConvertedResource
import opekope2.optigui.resource.OptiGuiResource
import opekope2.optigui.service.getService
import opekope2.util.OPTIFINE_RESOURCES_ROOT
import opekope2.util.OPTIGUI_NAMESPACE
import opekope2.util.OPTIGUI_RESOURCES_ROOT
import opekope2.util.component1
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
        manager.findResources(OPTIGUI_RESOURCES_ROOT) { name -> name.endsWith(".ini") }
            .filter { (ns) -> ns == OPTIGUI_NAMESPACE }
            .toSet()

    private fun findOptiFineResources(manager: ResourceManager) =
        manager.findResources(OPTIFINE_RESOURCES_ROOT) { name -> name.endsWith(".properties") }
            .filter { (ns) -> ns == Identifier.DEFAULT_NAMESPACE }
            .toSet()
}
