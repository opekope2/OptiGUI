package opekope2.filter

/**
 * A filter, which applies the logical OR operation between the given filters and returns the result.
 * Only skips if all sub-filters skip, and only yields mismatch if no sub-filters yield match.
 *
 * This filter yields the first non-skipping match result of all sub-filters if any,
 * or the first non-skipping mismatch result of all sub-filters if any,
 * or a new skipping filter result (when all sub-filters skip)
 *
 * @param T The type the filter accepts
 * @param filters The sub-filters to evaluate
 */
class DisjunctionFilter<T>(private val filters: Iterable<Filter<T, out Any>>) : Filter<T, Unit> {
    /**
     * Alternative constructor with variable arguments
     */
    constructor(vararg filters: Filter<T, out Any>) : this(filters.toList())

    override fun evaluate(value: T): FilterResult<Unit> = filters.map { it.evaluate(value) }.let { result ->
        if (result.any { it is FilterResult.Match }) FilterResult.Match(Unit)
        else if (result.all { it is FilterResult.Skip }) FilterResult.Skip()
        else FilterResult.Mismatch()
    }
}
