package opekope2.optigui.filter

/**
 * A filter, which returns a non-null value only when [regex] matches the input [String].
 *
 * @param regex The regular expression to filter with
 */
class RegularExpressionFilter(private val regex: Regex) : IFilter<String, Unit> {
    override fun evaluate(input: String) =
        if (regex.matches(input)) Unit
        else null

    override fun toString(): String = "${javaClass.name}, regex: ${regex.pattern}"
}
