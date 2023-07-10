package opekope2.optigui.properties

import net.minecraft.util.Identifier
import java.time.LocalDate

/**
 * Properties for lecterns.
 */
data class LecternProperties(
    override val container: Identifier,
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    override val currentPage: Int,
    override val pageCount: Int,
    override val date: LocalDate = LocalDate.now()
) : CommonProperties, BookBaseProperties
