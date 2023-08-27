package opekope2.optigui.properties.impl

import opekope2.optigui.properties.IIndependentProperties
import java.time.LocalDate

/**
 * Default implementation of [IIndependentProperties].
 */
data class IndependentProperties(
    override val date: LocalDate,
) : IIndependentProperties
