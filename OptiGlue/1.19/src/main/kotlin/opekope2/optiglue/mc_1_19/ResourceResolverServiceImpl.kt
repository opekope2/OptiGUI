package opekope2.optiglue.mc_1_19

import net.minecraft.client.MinecraftClient
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import opekope2.optigui.service.ResourceResolverService

class ResourceResolverServiceImpl : ResourceResolverService {
    private val manager: ResourceManager by lazy { MinecraftClient.getInstance().resourceManager }

    override fun resolveResource(id: Identifier?): Identifier? {
        if (resourceExists(id ?: return null)) return id

        val idPng = Identifier(id.namespace, "${id.path}.png")
        return if (resourceExists(idPng)) idPng else null
    }

    private fun resourceExists(id: Identifier): Boolean = manager.getResource(id).isPresent
}
