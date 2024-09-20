package opekope2.optigui.resource.matcher.nbt

import opekope2.optigui.filter.INbtFilter

/**
 * An NBT matcher, which takes exactly 1 parameter.
 */
fun interface ISingleParameterNbtMatcher : INbtMatcher {
    override fun createFilter(parameters: List<Any?>): INbtFilter {
        require(parameters.size == 1) { "Exactly 1 parameter is required, but ${parameters.size} was supplied" }
        return createFilter(parameters[0])
    }
}
