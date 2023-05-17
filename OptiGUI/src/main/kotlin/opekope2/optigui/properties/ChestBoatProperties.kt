package opekope2.optigui.properties

import net.minecraft.util.Identifier
import java.time.LocalDate

/**
 * Properties for chest boats.
 *
 * @param variant The variant of the chest boat:
 * `acacia`, `bamboo`, `birch`, `dark_oak`, `jungle`, `mangrove`, `oak`, or `spruce`
 */
data class ChestBoatProperties @JvmOverloads constructor(
    override val container: Identifier,
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    val variant: String,
    override val date: LocalDate = LocalDate.now()
) : CommonProperties
