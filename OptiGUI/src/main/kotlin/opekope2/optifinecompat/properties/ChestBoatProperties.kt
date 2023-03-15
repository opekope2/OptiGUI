package opekope2.optifinecompat.properties

import net.minecraft.util.Identifier

/**
 * Chest boat OptiFine container properties.
 *
 * @param variant The wood the boat is made of. Possible values:
 * `acacia`, `bamboo`, `birch`, `cherry`, `dark_oak`, `jungle`, `mangrove`, `oak`, `spruce`
 */
data class ChestBoatProperties(
    override val container: String,
    override val texture: Identifier,
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    val variant: String
) : GeneralProperties(container, texture, name, biome, height)
