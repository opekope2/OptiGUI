package opekope2.optigui.filter

/**
 * A filter, which applies the logical AND operation between the given filters and returns the result.
 * Only skips if all sub-filters skip, and only yields match if no sub-filters yield mismatch.
 *
 * @param T The type the filter accepts
 * @param filters The sub-filters to evaluate
 */
class ConjunctionFilter<T>(private val filters: Iterable<Filter<T, *>>) : Filter<T, Unit>(), Iterable<Filter<T, *>> {
    /**
     * Alternative constructor with variable arguments
     */
    constructor(vararg filters: Filter<T, *>) : this(filters.toList())

    override fun evaluate(value: T): FilterResult<Unit> = filters.map { it.evaluate(value) }.let { result ->
        if (result.any { it is FilterResult.Mismatch }) FilterResult.Mismatch()
        else if (result.all { it is FilterResult.Skip }) FilterResult.Skip()
        else FilterResult.Match(Unit)
    }

    override fun iterator(): Iterator<Filter<T, *>> = filters.iterator()

    override fun toString(): String = javaClass.name
}
