package opekope2.optiglue

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import opekope2.optiglue.mc_1_18_2.ResourceGlue
import opekope2.optigui.provider.getProvider
import opekope2.optigui.internal.ResourceLoader as OptiGUIResourceLoader

internal object ResourceLoader : SimpleSynchronousResourceReloadListener {
    private val resourceLoader = getProvider<OptiGUIResourceLoader>()

    override fun getFabricId() = Identifier("optiglue", "optigui_resource_loader")

    override fun reload(manager: ResourceManager) {
        val resources =
            manager.findResources("optifine/gui/container") { it.endsWith(".properties") }
                .map { ResourceGlue(manager, it) }

        resourceLoader.loadResources(resources)
    }
}
