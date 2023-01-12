package opekope2.filter

/**
 * A filter which applies the logical AND operation between the given filters and returns the result.
 * Only skips if all sub-filters skip, and only yields match if no sub-filters yield mismatch.
 *
 * This filter yields the first non-skipping mismatch result of all sub-filters if any,
 * or the first non-skipping match result of all sub-filters if any,
 * or a new skipping filter result (when all sub-filters skip)
 *
 * @param T The type the filter accepts
 * @param filters The sub-filters to evaluate
 */
class ConjunctionFilter<T>(private val filters: Iterable<Filter<T, out Any>>) : Filter<T, Unit>() {
    /**
     * Alternative constructor with variable arguments
     */
    constructor(vararg filters: Filter<T, out Any>) : this(filters.toList())

    override fun test(value: T): FilterResult<Unit> = filters.map { it.test(value) }.let { result ->
        if (result.all { it.skip }) FilterResult(skip = true)
        else FilterResult(skip = false, match = result.none { !it.skip && !it.match })
    }
}
