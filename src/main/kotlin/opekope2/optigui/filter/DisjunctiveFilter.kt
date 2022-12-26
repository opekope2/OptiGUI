package opekope2.optigui.filter

/**
 * A filter which applies the logical OR operation between the given filters and returns the result.
 * Only skips if all sub-filters skip.
 *
 * This filter doesn't yield a replacement.
 *
 * @param T The type the filter accepts
 * @param filters The sub-filters to evaluate
 */
class DisjunctiveFilter<T>(private val filters: Iterable<Filter<T>>) : Filter<T>() {
    override fun test(value: T): FilterResult<out T> = filters.map { it.test(value) }.let { results ->
        if (results.all { it.skip }) FilterResult(skip = true)
        else FilterResult(results.any { !it.skip && it.match })
    }
}
