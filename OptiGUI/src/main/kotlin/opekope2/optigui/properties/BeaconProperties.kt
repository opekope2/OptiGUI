package opekope2.optigui.properties

import net.minecraft.util.Identifier
import java.time.LocalDate

/**
 * Properties for beacons.
 *
 * @param level The level of a beacon
 */
data class BeaconProperties @JvmOverloads constructor(
    override val container: Identifier,
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    val level: Int,
    override val date: LocalDate = LocalDate.now()
) : CommonProperties
