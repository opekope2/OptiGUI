package opekope2.optigui.resource

import net.minecraft.util.Identifier
import opekope2.optigui.exception.ResourceNotFoundException

/**
 * A base class for resources.
 *
 * @param id The identifier of the resource
 */
abstract class Resource(val id: Identifier) {
    /**
     * Returns if the current resource exists.
     */
    abstract fun exists(): Boolean

    /**
     * Returns the name of the resource pack the current resource is loaded from.
     *
     * @throws ResourceNotFoundException If the resource doesn't exist
     */
    @get: Throws(ResourceNotFoundException::class)
    abstract val resourcePack: String
}
