package opekope2.optifinecompat.properties

import net.minecraft.util.Identifier

/**
 * General OptiFine container properties.
 *
 * @param name Custom entity or block entity name
 * @param biome Biomes of a block entity or entity
 * @param height Y coordinate of a block entity or entity
 */
abstract class GeneralProperties(
    open val name: String?,
    open val biome: Identifier?,
    open val height: Int
)
