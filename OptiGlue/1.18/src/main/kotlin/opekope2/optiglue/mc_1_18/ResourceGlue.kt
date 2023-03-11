package opekope2.optiglue.mc_1_18

import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import opekope2.optigui.exception.ResourceNotFoundException
import opekope2.optigui.resource.Resource
import java.util.*
import net.minecraft.resource.Resource as MC_Resource

internal class ResourceGlue(private val manager: ResourceManager, id: Identifier) : Resource(id) {
    private val resource: MC_Resource? by lazy { if (exists()) manager.getResource(id) else null }

    override fun exists(): Boolean = manager.containsResource(id)
    override val resourcePack: String = resource?.resourcePackName ?: throw ResourceNotFoundException(id)
    override val properties: Properties by lazy {
        Properties().apply {
            load(resource?.inputStream ?: throw ResourceNotFoundException(id))
        }
    }
}
