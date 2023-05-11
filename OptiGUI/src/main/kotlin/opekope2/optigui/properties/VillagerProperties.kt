package opekope2.optigui.properties

import net.minecraft.util.Identifier
import java.time.LocalDate

/**
 * Properties for villagers.
 *
 * @param profession The profession of a villager
 * @param level The level of a villager
 */
data class VillagerProperties @JvmOverloads constructor(
    override val container: Identifier,
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    val profession: Identifier,
    val level: Int,
    override val date: LocalDate = LocalDate.now()
) : CommonProperties
