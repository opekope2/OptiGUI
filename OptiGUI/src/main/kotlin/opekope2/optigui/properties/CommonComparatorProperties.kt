package opekope2.optigui.properties

import net.minecraft.util.Identifier
import java.time.LocalDate

/**
 * Default implementation for [ComparatorProperties].
 */
data class CommonComparatorProperties @JvmOverloads constructor(
    override val container: Identifier,
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    override val date: LocalDate = LocalDate.now(),
    override val comparatorOutput: Int
) : CommonProperties, ComparatorProperties
