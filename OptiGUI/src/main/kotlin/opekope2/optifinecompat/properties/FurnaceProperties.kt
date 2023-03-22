package opekope2.optifinecompat.properties

import net.minecraft.util.Identifier

/**
 * Furnace OptiFine container properties.
 *
 * @param variant nothing, `_furnace`, `_blast`, `_blast_furnace`, or `smoker`
 */
data class FurnaceProperties(
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    val variant: String
) : GeneralProperties(name, biome, height)
