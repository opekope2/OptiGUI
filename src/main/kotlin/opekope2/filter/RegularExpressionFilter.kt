package opekope2.filter

/**
 * A filter, yields a successful result only, when the provided regular expression matches the input.
 *
 * @param regex The regular expression to filter with
 */
class RegularExpressionFilter(private val regex: Regex) : Filter<String, Unit>() {
    override fun test(value: String): FilterResult<out Unit> = FilterResult(skip = false, match = regex.matches(value))
}