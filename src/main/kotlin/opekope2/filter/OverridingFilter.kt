package opekope2.filter

/**
 * A filter which returns the result of the sub-filter, and overrides its result.
 *
 * Useful to override the sub-filter's result,
 * or when the sub-filter doesn't yield a result (like [ConjunctionFilter] or [DisjunctionFilter])
 *
 * @param filter The filter to evaluate
 * @param result The result to set after evaluating [filter]
 * @param T The type the filter accepts
 * @param TResult the type the filter returns
 */
class OverridingFilter<T, TResult>(private val filter: Filter<T, out Any>, private val result: TResult) :
    Filter<T, TResult>() {
    override fun test(value: T) = filter.test(value).let { FilterResult(it.skip, it.match, result) }
}
