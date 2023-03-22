package opekope2.optifinecompat.properties

import net.minecraft.util.Identifier

/**
 * Dispenser OptiFine container properties.
 *
 * @param variant `dispenser` or `dropper`
 */
data class DispenserProperties(
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    val variant: String
) : GeneralProperties(name, biome, height)
