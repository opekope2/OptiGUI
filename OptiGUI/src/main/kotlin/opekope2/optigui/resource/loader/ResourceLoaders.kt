package opekope2.optigui.resource.loader

import net.minecraft.util.Identifier
import opekope2.optigui.registry.RegistryBase

/**
 * OptiGUI resource loader registry.
 */
object ResourceLoaders : RegistryBase<Identifier, IResourceLoader<*>>()
