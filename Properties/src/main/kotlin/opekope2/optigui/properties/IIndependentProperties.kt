package opekope2.optigui.properties

import java.time.LocalDate

/**
 * Common properties, which are not dependent on block entities or entities.
 *
 * @see opekope2.optigui.properties.impl.IndependentProperties
 */
interface IIndependentProperties {
    /**
     * The current date.
     */
    val date: LocalDate
}
