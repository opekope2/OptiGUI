package opekope2.optigui.provider

import net.minecraft.resource.ResourceManager
import opekope2.optigui.resource.IResourceManager

interface IResourceManagerProvider {
    fun wrapResourceManager(resourceManager: ResourceManager): IResourceManager
}
