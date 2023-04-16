package opekope2.optiglue

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import opekope2.optiglue.mc_1_19_3.GlueResource
import opekope2.optigui.internal.service.ResourceLoaderService
import opekope2.optigui.service.getService
import opekope2.util.component1
import opekope2.util.component2

internal object ResourceLoader : SimpleSynchronousResourceReloadListener {
    private val resourceLoader = getService<ResourceLoaderService>()

    override fun getFabricId() = Identifier("optiglue", "optigui_resource_loader")

    override fun reload(manager: ResourceManager) {
        val resources =
            manager.findResources("optifine/gui/container")
            { (ns, path) -> ns == Identifier.DEFAULT_NAMESPACE && path.endsWith(".properties") }
                .map { (key) -> GlueResource(key) } union
                    manager.findResources("")
                    { (ns, path) -> ns == "optigui" && path.endsWith(".ini") }
                        .map { (key) -> GlueResource(key) }

        resourceLoader.loadResources(resources)
    }
}
