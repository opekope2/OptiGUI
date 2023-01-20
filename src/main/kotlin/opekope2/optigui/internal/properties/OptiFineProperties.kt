package opekope2.optigui.internal.properties

import net.minecraft.util.Identifier

internal data class OptiFineProperties(
    override val container: String,
    override val texture: Identifier,
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int
) : GeneralProperties(container, texture, name, biome, height)
