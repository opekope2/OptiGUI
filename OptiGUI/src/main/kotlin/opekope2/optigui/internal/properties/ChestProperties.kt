package opekope2.optigui.internal.properties

import net.minecraft.util.Identifier

internal data class ChestProperties(
    override val container: String,
    override val texture: Identifier,
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    val large: Boolean,
    val trapped: Boolean,
    val christmas: Boolean,
    val ender: Boolean,
    val barrel: Boolean
) : GeneralProperties(container, texture, name, biome, height)
