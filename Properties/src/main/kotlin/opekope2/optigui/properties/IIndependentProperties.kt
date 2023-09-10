package opekope2.optigui.properties

import opekope2.optigui.api.interaction.IInteractionData
import java.time.LocalDate
import java.util.function.BiConsumer

/**
 * Common properties, which are not dependent on block entities or entities.
 *
 * @see opekope2.optigui.properties.impl.IndependentProperties
 */
interface IIndependentProperties : IInteractionData {
    /**
     * The current date.
     */
    val date: LocalDate

    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
        appendSelector.accept("date", "${date.month.name.lowercase()}@${date.dayOfMonth}")
    }
}
