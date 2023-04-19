package opekope2.optigui.properties

import net.minecraft.util.Identifier

/**
 * Properties for villagers.
 *
 * @param profession The profession of a villager
 * @param level The level of a villager
 */
data class VillagerProperties(
    override val container: Identifier,
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    val profession: Identifier,
    val level: Int
) : CommonProperties
