package opekope2.optigui.properties

import net.minecraft.util.Identifier

/**
 * Default implementation of [CommonProperties].
 */
data class DefaultProperties(
    override val container: Identifier,
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int
) : CommonProperties
