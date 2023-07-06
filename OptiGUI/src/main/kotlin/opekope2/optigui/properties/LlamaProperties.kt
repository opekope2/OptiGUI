package opekope2.optigui.properties

import net.minecraft.util.Identifier
import java.time.LocalDate

/**
 * Properties for llamas.
 *
 * @param carpetColor The carpet color of a llama or `null`, if it doesn't have a carpet.
 */
data class LlamaProperties @JvmOverloads constructor(
    override val container: Identifier,
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    val carpetColor: String?,
    override val date: LocalDate = LocalDate.now(),
    override val hasChest: Boolean = false
) : CommonProperties, DonkeyBaseProperties
