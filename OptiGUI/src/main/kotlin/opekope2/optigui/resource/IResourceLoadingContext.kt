package opekope2.optigui.resource

import net.minecraft.resource.Resource
import net.minecraft.util.Identifier
import opekope2.optigui.filter.IInteractionFilter
import org.slf4j.Logger

/**
 * Information about the resource being loaded.
 *
 * @param T The type of the loaded resource
 */
interface IResourceLoadingContext<T> {
    /**
     * The Id of the resource currently being loaded.
     */
    val resourceId: Identifier

    /**
     * The loaded resource.
     */
    val resource: T

    /**
     * The logger used to log resource loading events.
     */
    val logger: Logger

    /**
     * Gets a resource from Minecraft.
     *
     * @param resourceId The resource ID to get
     */
    fun getResource(resourceId: Identifier): Resource?

    /**
     * Gets a loaded [resource] from possibly other [IResourceLoadingContext]s.
     *
     * @param resourceId The resource ID to get
     */
    fun getLoadedResource(resourceId: Identifier): Any?

    /**
     * Marks a texture ID as replaceable.
     *
     * @param textureId The texture ID to mark as replaceable
     */
    fun addReplaceableTexture(textureId: Identifier)

    /**
     * Tells OptiGUI to replace the texture of a container with the given texture if the given filter matches the
     * interaction.
     *
     * @param containerId The container to replace the texture of
     * @param filter
     * @param replacementTexture The texture to replace the container's texture with
     * @param priority The evaluation order of the filter. Can't be negative. Higher priority filters are evaluated
     *  first. OptiGUI works best if most filters have 0 priority, so only set this anything higher if you must
     */
    fun addRetexturableContainer(
        containerId: Identifier,
        filter: IInteractionFilter,
        replacementTexture: Identifier,
        priority: Int
    )
}
