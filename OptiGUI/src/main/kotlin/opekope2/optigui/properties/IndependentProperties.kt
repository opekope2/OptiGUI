package opekope2.optigui.properties

import java.time.LocalDate

/**
 * Common properties, which are not dependent on block entities or entities
 */
interface IndependentProperties {
    /**
     * The current date
     */
    val date: LocalDate
}
