package opekope2.optifinecompat.properties

import net.minecraft.util.Identifier

/**
 * Hopper OptiFine container properties.
 *
 * @param isMinecart Whether a hopper is a hopper minecart
 */
data class HopperProperties(
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    val isMinecart: Boolean
) : GeneralProperties(name, biome, height)
