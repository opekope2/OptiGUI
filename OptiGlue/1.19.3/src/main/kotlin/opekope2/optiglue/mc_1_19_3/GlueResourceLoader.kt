package opekope2.optiglue.mc_1_19_3

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import opekope2.optigui.internal.ResourceLoader
import opekope2.optigui.provider.getProvider

internal object GlueResourceLoader : SimpleSynchronousResourceReloadListener {
    private val resourceLoader = getProvider<ResourceLoader>()

    override fun getFabricId() = Identifier("optiglue", "optigui_resource_loader")

    override fun reload(manager: ResourceManager) {
        val resources =
            manager.findResources("optifine/gui/container") { it.path.endsWith(".properties") }
                .map { GlueResource(manager, it.key) }

        resourceLoader.loadResources(resources)
    }
}
