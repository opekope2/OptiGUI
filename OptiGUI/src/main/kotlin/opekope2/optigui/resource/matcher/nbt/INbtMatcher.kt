package opekope2.optigui.resource.matcher.nbt

import opekope2.optigui.filter.INbtFilter

/**
 * An NBT matcher creates an [INbtFilter] from values supplied in a resource.
 */
interface INbtMatcher {
    /**
     * Creates an [INbtFilter] from a single parameter or throws an exception.
     *
     * @param parameter The resource-supplied parameter
     */
    fun createFilter(parameter: Any?): INbtFilter

    /**
     * Creates an [INbtFilter] from a list of parameters or throws an exception.
     *
     * @param parameters The resource-supplied list containing the parameters
     */
    fun createFilter(parameters: List<Any?>): INbtFilter
}
