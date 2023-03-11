package opekope2.optifinecompat.properties

import net.minecraft.util.Identifier

/**
 * Hopper OptiFine container properties.
 *
 * @param isMinecart Whether a hopper is a hopper minecart
 */
data class HopperProperties(
    override val container: String,
    override val texture: Identifier,
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    val isMinecart: Boolean
) : GeneralProperties(container, texture, name, biome, height)
