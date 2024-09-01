package opekope2.optigui.resource.matcher.nbt

/**
 * An NBT matcher, which takes exactly 1 parameter.
 */
fun interface ISingleParameterNbtMatcher : INbtMatcher {
    override fun createFilter(parameters: List<Any?>) =
        if (parameters.size == 1) createFilter(parameters[0])
        else throw IllegalArgumentException("Exactly 1 parameter is required, but ${parameters.size} was supplied")
}
