package opekope2.optigui.filter

/**
 * A filter, which returns the opposite result of the provided filter
 *
 * @param T the type the filter accepts
 * @param filter the filter to negate
 * @param replacement the optional replacement to set when the negated filter becomes a match
 */
class NegativeFilter<T> @JvmOverloads constructor(private val filter: Filter<T>, private val replacement: T? = null) :
    Filter<T>() {
    override fun test(value: T): FilterResult<out T> {
        val result = filter.test(value)
        if (result.skip) return result

        return FilterResult.create(!result.match, replacement)
    }
}
