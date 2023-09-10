package opekope2.optigui.filter

/**
 * A filter, yields a successful result only, when the provided regular expression matches the input.
 *
 * @param regex The regular expression to filter with
 */
class RegularExpressionFilter(private val regex: Regex) : IFilter<String, Unit> {
    override fun evaluate(value: String): IFilter.Result<out Unit> =
        if (regex.matches(value)) IFilter.Result.match(Unit) else IFilter.Result.mismatch()

    override fun toString(): String = "${javaClass.name}, regex: ${regex.pattern}"
}
