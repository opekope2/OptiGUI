package opekope2.optigui.internal.properties

import net.minecraft.util.Identifier

/**
 * Based on https://stackoverflow.com/a/55701197
 */
internal abstract class GeneralProperties(
    open val container: String,
    open val texture: Identifier,
    open val name: String?,
    open val biome: Identifier?,
    open val height: Int
)
