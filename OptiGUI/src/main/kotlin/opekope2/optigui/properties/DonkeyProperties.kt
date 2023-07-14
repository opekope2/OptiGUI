package opekope2.optigui.properties

import net.minecraft.util.Identifier
import java.time.LocalDate

/**
 * Default implementation of [DonkeyBaseProperties]
 */
data class DonkeyProperties(
    override val hasChest: Boolean,
    override val container: Identifier,
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    override val date: LocalDate = LocalDate.now()
) : CommonProperties, DonkeyBaseProperties
