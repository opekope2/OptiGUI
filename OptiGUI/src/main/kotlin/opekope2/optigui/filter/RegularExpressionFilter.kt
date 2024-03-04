package opekope2.optigui.filter

import opekope2.optigui.filter.IFilter.Result.Match
import opekope2.optigui.filter.IFilter.Result.Mismatch

/**
 * A filter, which yields a successful result only, when the provided regular expression matches the input.
 *
 * @param regex The regular expression to filter with
 */
class RegularExpressionFilter(private val regex: Regex) : IFilter<String, Unit> {
    override fun evaluate(value: String): IFilter.Result<out Unit> =
        if (regex.matches(value)) Match(Unit)
        else Mismatch

    override fun toString(): String = "${javaClass.name}, regex: ${regex.pattern}"
}
