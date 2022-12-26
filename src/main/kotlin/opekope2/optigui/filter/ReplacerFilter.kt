package opekope2.optigui.filter

/**
 * A filter which returns the result of the sub-filter, and replaces its replacement.
 *
 * Useful to override the sub-filter's replacement,
 * or when the sub-filter doesn't yield a replacement (like [ConjunctiveFilter] or [DisjunctiveFilter])
 */
class ReplacerFilter<T>(private val filter: Filter<T>, private val replacement: T) : Filter<T>() {
    override fun test(value: T): FilterResult<out T> =
        filter.test(value).let { FilterResult(it.skip, it.match, replacement) }
}
