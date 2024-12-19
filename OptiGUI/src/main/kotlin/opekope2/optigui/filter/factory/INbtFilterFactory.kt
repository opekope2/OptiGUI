package opekope2.optigui.filter.factory

import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.registry.RegistryBase
import org.slf4j.Logger

/**
 * A factory for an [INbtFilter].
 */
fun interface INbtFilterFactory {
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
    ): INbtFilter?

    /**
     * Interaction filter registry.
     */
    companion object Registry : RegistryBase<String, INbtFilterFactory>() {
        override fun validateEntry(key: String, value: INbtFilterFactory) {
            super.validateEntry(key, value)
            require(key != "if") { "Key must not be `if`" }
            require(!key.startsWith("if.")) { "Key can't start with `if.`" }
        }
    }
}
