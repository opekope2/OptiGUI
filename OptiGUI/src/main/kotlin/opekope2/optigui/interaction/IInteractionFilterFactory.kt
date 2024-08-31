package opekope2.optigui.interaction

import opekope2.optigui.filter.IInteractionFilter
import opekope2.optigui.filter.INbtFilter
import org.slf4j.Logger

/**
 * A factory for an [IInteractionFilter].
 */
fun interface IInteractionFilterFactory {
    /**
     * Creates a filter, which filters an interaction.
     *
     * @param parameters Parameter array from a resource
     * @param properties Named parameters from a resource
     * @param childFilters Child filters parsed from a resource
     * @param logger The logger to log warnings and errors to. An error must be logged if `null` is returned to tell the
     *  user about the failure
     */
    fun createInteractionFilter(
        parameters: List<Any?>,
        properties: Map<String, Any?>,
        childFilters: Collection<INbtFilter>,
        logger: Logger
    ): IInteractionFilter?
}
