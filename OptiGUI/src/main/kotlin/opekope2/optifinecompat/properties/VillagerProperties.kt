package opekope2.optifinecompat.properties

import net.minecraft.util.Identifier

/**
 * Villager OptiFine container properties.
 *
 * @param profession The profession of a villager
 * @param level The level of a villager
 */
data class VillagerProperties(
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    val profession: Identifier,
    val level: Int
) : GeneralProperties(name, biome, height)
