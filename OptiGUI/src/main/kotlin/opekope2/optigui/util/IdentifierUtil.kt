package opekope2.optigui.util

import net.minecraft.util.Identifier

/**
 * [Identifier] deconstruction helper, which returns the namespace.
 *
 * @see Identifier.namespace
 */
operator fun Identifier.component1(): String = namespace

/**
 * [Identifier] deconstruction helper, which returns the path.
 *
 * @see Identifier.path
 */
operator fun Identifier.component2(): String = path
