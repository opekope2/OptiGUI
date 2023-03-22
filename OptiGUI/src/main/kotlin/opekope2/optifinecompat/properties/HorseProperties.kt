package opekope2.optifinecompat.properties

import net.minecraft.util.Identifier

/**
 * Horse OptiFine container properties.
 *
 * @param variant `horse`, `donkey`, `mule`, `llama`, `_camel`, `_zombie_horse`, or `_skeleton_horse`
 * @param carpetColor The carpet color of a llama
 */
data class HorseProperties(
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    val variant: String,
    val carpetColor: String?
) : GeneralProperties(name, biome, height)
