package opekope2.filter

/**
 * A filter which applies the logical OR operation between the given filters and returns the result.
 * Only skips if all sub-filters skip, and only yields mismatch if no sub-filters yield match.
 *
 * This filter yields the first non-skipping match result of all sub-filters if any,
 * or the first non-skipping mismatch result of all sub-filters if any,
 * or a new skipping filter result (when all sub-filters skip)
 *
 * @param T The type the filter accepts
 * @param filters The sub-filters to evaluate
 */
class DisjunctiveFilter<T>(private val filters: Iterable<Filter<T, out Any>>) : Filter<T, Unit>() {
    override fun test(value: T): FilterResult<Unit> = filters.map { it.test(value) }.let { result ->
        if (result.all { it.skip }) FilterResult(skip = true)
        else FilterResult(skip = false, match = result.any { !it.skip && it.match })
    }
}
