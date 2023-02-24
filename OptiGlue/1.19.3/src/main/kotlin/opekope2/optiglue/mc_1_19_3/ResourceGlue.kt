package opekope2.optiglue.mc_1_19_3

import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import opekope2.optigui.exception.ResourceNotFoundException
import opekope2.optigui.resource.Resource
import java.util.*
import kotlin.jvm.optionals.getOrNull

internal class ResourceGlue(manager: ResourceManager, id: Identifier) : Resource(id) {
    private val resource = manager.getResource(id).getOrNull()

    override fun exists(): Boolean = resource != null
    override val resourcePack: String = resource?.resourcePackName ?: throwResourceNotFound()
    override val properties: Properties by lazy {
        Properties().apply {
            load(resource?.inputStream ?: throwResourceNotFound())
        }
    }

    private fun throwResourceNotFound(): Nothing = throw ResourceNotFoundException("Resource '$id' doesn't exist!")
}
