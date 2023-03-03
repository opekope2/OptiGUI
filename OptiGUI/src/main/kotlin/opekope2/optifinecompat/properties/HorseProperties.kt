package opekope2.optifinecompat.properties

import net.minecraft.util.Identifier

/**
 * Horse OptiFine container properties.
 *
 * @param variant `horse`, `donkey`, `mule`, `llama`, `_camel`, `_zombie_horse`, or `_skeleton_horse`
 */
data class HorseProperties(
    override val container: String,
    override val texture: Identifier,
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    val variant: String
) : GeneralProperties(container, texture, name, biome, height)
