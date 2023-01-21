package opekope2.optigui.provider

import net.minecraft.util.Identifier

/**
 * An interface defining a resource resolver. A resolver looks for the given resource, and returns it
 * (or possible candidates) when found, or `null`, if not found.
 */
fun interface ResourceResolver {
    /**
     * Finds the given resource.
     *
     * @param id The resource to find
     * @return The found resource or `null` if not found
     */
    fun resolveResource(id: Identifier?): Identifier?
}
