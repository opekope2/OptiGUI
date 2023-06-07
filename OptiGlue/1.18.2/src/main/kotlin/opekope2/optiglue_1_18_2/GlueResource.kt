package opekope2.optiglue_1_18_2

import net.minecraft.client.MinecraftClient
import net.minecraft.resource.Resource
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import opekope2.optigui.exception.ResourceNotFoundException
import opekope2.optigui.resource.ResourceReader
import opekope2.optigui.service.ResourceAccessService
import java.io.InputStream

internal class GlueResource(id: Identifier) : ResourceReader(id) {
    private val resource: Resource? by lazy { if (exists()) manager.getResource(id) else null }

    override fun exists(): Boolean = manager.containsResource(id)
    override val resourcePack: String
        get() = resource?.resourcePackName ?: throw ResourceNotFoundException(id)
    override val inputStream: InputStream
        get() = resource?.inputStream ?: throw ResourceNotFoundException(id)

    override fun close() {
        resource?.close()
    }

    companion object : ResourceAccessService {
        private val manager: ResourceManager by lazy { MinecraftClient.getInstance().resourceManager }

        override fun getResource(id: Identifier): ResourceReader = GlueResource(id)
    }
}
