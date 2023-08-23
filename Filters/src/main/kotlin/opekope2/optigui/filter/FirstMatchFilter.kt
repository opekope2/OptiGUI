package opekope2.optigui.filter

/**
 * A filter, which returns the first [FilterResult.Match] result from [filters].
 * Only skips if all sub-filters skip, and only yields mismatch if no sub-filters yield match.
 *
 * @param T The type the filter accepts
 * @param TResult The type the filter returns
 * @param filters The sub-filters to evaluate
 */
class FirstMatchFilter<T, TResult>(private val filters: Iterable<Filter<T, out TResult>>) :
    Filter<T, TResult>(), Iterable<Filter<T, out TResult>> {
    /**
     * Alternative constructor with variable arguments
     */
    constructor(vararg filters: Filter<T, out TResult>) : this(filters.toList())

    override fun evaluate(value: T): FilterResult<out TResult> = filters.map { it.evaluate(value) }.let { results ->
        if (results.all { it is FilterResult.Skip }) FilterResult.Skip()
        else results.firstOrNull { it is FilterResult.Match } ?: FilterResult.Mismatch()
    }

    override fun iterator(): Iterator<Filter<T, out TResult>> = filters.iterator()

    override fun toString(): String = javaClass.name
}
