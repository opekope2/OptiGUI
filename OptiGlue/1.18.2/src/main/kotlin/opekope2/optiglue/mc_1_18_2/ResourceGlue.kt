package opekope2.optiglue.mc_1_18_2

import net.minecraft.resource.Resource
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import opekope2.optigui.exception.ResourceNotFoundException
import opekope2.optigui.resource.ResourceReader
import java.io.InputStream

internal class ResourceGlue(private val manager: ResourceManager, id: Identifier) : ResourceReader(id) {
    private val resource: Resource? by lazy { if (exists()) manager.getResource(id) else null }

    override fun exists(): Boolean = manager.containsResource(id)
    override val resourcePack: String = resource?.resourcePackName ?: throw ResourceNotFoundException(id)
    override val inputStream: InputStream = resource?.inputStream ?: throw ResourceNotFoundException(id)
}
