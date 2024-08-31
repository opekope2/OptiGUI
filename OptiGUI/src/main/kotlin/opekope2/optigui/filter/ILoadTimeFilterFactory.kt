package opekope2.optigui.filter

import org.slf4j.Logger

/**
 * A factory for an [ILoadTimeFilter].
 */
fun interface ILoadTimeFilterFactory {
    /**
     * Creates a filter, which is evaluated at load-time to determine if the resource should be loaded.
     *
     * @param parameters Parameter array from a resource
     * @param properties Named parameters from a resource
     * @param childFilters Child filters parsed from a resource
     * @param logger The logger to log warnings and errors to. An error must be logged if `null` is returned to tell the
     *  user about the failure
     */
    fun createLoadTimeFilter(
        parameters: List<Any?>,
        properties: Map<String, Any?>,
        childFilters: Collection<INbtFilter>,
        logger: Logger
    ): ILoadTimeFilter?
}
