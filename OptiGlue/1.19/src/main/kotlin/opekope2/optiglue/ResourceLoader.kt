package opekope2.optiglue

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import opekope2.optiglue.mc_1_19.ResourceGlue
import opekope2.optigui.service.getService
import opekope2.optigui.internal.ResourceLoader as OptiGUIResourceLoader

internal object ResourceLoader : SimpleSynchronousResourceReloadListener {
    private val resourceLoader = getService<OptiGUIResourceLoader>()

    override fun getFabricId() = Identifier("optiglue", "optigui_resource_loader")

    override fun reload(manager: ResourceManager) {
        val resources =
            manager.findResources("optifine/gui/container") { it.path.endsWith(".properties") }
                .map { ResourceGlue(manager, it.key) }

        resourceLoader.loadResources(resources)
    }
}
