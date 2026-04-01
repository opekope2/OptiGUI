package opekope2.optigui.util

import net.minecraft.resources.ResourceLocation

/**
 * [Identifier] deconstruction helper, which returns the namespace.
 *
 * @see Identifier.namespace
 */
operator fun ResourceLocation.component1(): String = namespace

/**
 * [Identifier] deconstruction helper, which returns the path.
 *
 * @see Identifier.path
 */
operator fun ResourceLocation.component2(): String = path
