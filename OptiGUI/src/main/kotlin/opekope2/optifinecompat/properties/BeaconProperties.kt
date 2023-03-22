package opekope2.optifinecompat.properties

import net.minecraft.util.Identifier

/**
 * Beacon OptiFine container properties.
 *
 * @param level The level of a beacon
 */
data class BeaconProperties(
    override val container: String,
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    val level: Int
) : GeneralProperties(container, name, biome, height)
