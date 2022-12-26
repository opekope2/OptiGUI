package opekope2.optigui.filter

/**
 * A filter which applies the logical OR operation between the given filters and returns the result.
 * Only skips if all sub-filters skip.
 *
 * @param T The type the filter accepts
 * @param filters The sub-filters to evaluate
 */
class DisjunctiveFilter<T>(private val filters: Iterable<Filter<T>>) : Filter<T>() {
    override fun test(value: T): FilterResult<T> {
        val results = filters.map { it.test(value) }
        return if (results.all { it.skip }) FilterResult.skip()
        else FilterResult.create(results.any { !it.skip && it.match })
    }
}
