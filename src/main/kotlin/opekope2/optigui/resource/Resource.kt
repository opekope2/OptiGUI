package opekope2.optigui.resource

import net.minecraft.util.Identifier
import opekope2.optigui.exception.ResourceNotFoundException
import java.util.*

/**
 * A wrapper class for [net.minecraft.resource.Resource]
 *
 * @param resourceManager A wrapper for [net.minecraft.resource.ResourceManager]
 * @param id The identifier of the resource
 */
class Resource(val resourceManager: IResourceManager, val id: Identifier) {
    /**
     * Returns if the current resource exists or not
     */
    val exists = resourceManager.resourceExists(id)

    /**
     * Returns the name of the resource pack the current resource is loaded from.
     *
     * @throws ResourceNotFoundException If [exists] is `false`
     */
    val resourcePack: String by lazy { if (exists) resourceManager.resourcePackName(id) else throwResourceNotFound() }

    /**
     * Returns the parsed `.properties` file.
     *
     * @throws ResourceNotFoundException If [exists] is `false`
     */
    val properties: Properties by lazy {
        if (exists) Properties().apply { load(resourceManager.getInputStream(id)) } else throwResourceNotFound()
    }

    private fun throwResourceNotFound(): Nothing =
        throw ResourceNotFoundException("Resource '${id}' doesn't exist!")
}
