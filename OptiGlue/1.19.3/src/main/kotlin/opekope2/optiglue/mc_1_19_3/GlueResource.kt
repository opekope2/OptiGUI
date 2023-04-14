package opekope2.optiglue.mc_1_19_3

import net.minecraft.client.MinecraftClient
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import opekope2.optigui.exception.ResourceNotFoundException
import opekope2.optigui.resource.ResourceReader
import opekope2.optigui.service.ResourceAccessService
import java.io.InputStream
import kotlin.jvm.optionals.getOrNull

internal class GlueResource(id: Identifier) : ResourceReader(id) {
    private val resource = manager.getResource(id).getOrNull()

    override fun exists(): Boolean = resource != null
    override val resourcePack: String = resource?.resourcePackName ?: throw ResourceNotFoundException(id)
    override val inputStream: InputStream = resource?.inputStream ?: throw ResourceNotFoundException(id)

    companion object : ResourceAccessService {
        private val manager: ResourceManager by lazy { MinecraftClient.getInstance().resourceManager }

        override fun getResource(id: Identifier): ResourceReader = GlueResource(id)
    }
}
