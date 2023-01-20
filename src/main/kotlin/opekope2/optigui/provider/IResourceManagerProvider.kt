package opekope2.optigui.provider

import net.minecraft.resource.ResourceManager
import opekope2.optigui.resource.IResourceManager

/**
 * Interface for wrapping a Minecraft resource manager.
 */
interface IResourceManagerProvider {
    /**
     * Wraps the given [resourceManager].
     */
    fun wrapResourceManager(resourceManager: ResourceManager): IResourceManager
}
