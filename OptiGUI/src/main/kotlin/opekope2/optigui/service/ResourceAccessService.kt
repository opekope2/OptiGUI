package opekope2.optigui.service

import net.minecraft.util.Identifier
import opekope2.optigui.resource.ResourceReader

/**
 * A resource accessor interface.
 */
interface ResourceAccessService {
    /**
     * Finds the given resource.
     *
     * @param id The resource to find
     * @return The representation of the resource
     */
    fun getResource(id: Identifier): ResourceReader
}
