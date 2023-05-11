package opekope2.filter.factory

import opekope2.optigui.resource.OptiGuiResource

/**
 * An object holding the [resource] and providing resource logging functionality.
 */
abstract class FilterFactoryContext {
    /**
     * Returns the resource to load in a [FilterFactory].
     */
    abstract val resource: OptiGuiResource

    /**
     * Logs an unexpected warning, which is caused by an incomplete/malformed/missing/etc resource.
     *
     * @param message The message to log
     */
    abstract fun warn(message: String)
}
