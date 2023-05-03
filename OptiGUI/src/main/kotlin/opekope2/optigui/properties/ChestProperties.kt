package opekope2.optigui.properties

import net.minecraft.util.Identifier
import java.time.LocalDate

/**
 * Properties for chests and trapped chests.
 *
 * @param isLarge Whether a chest is large
 */
data class ChestProperties @JvmOverloads constructor(
    override val container: Identifier,
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    val isLarge: Boolean,
    override val date: LocalDate = LocalDate.now(),
) : CommonProperties
