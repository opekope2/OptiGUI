package opekope2.optigui.properties

import net.minecraft.util.Identifier

/**
 * Properties for beacons.
 *
 * @param level The level of a beacon
 */
data class BeaconProperties(
    override val container: Identifier,
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    val level: Int
) : CommonProperties
