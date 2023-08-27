package opekope2.optigui.filter

/**
 * A filter, which applies the logical OR operation between the given filters and returns the result.
 * Only skips if all sub-filters skip, and only yields mismatch if no sub-filters yield match.
 *
 * @param T The type the filter accepts
 * @param filters The sub-filters to evaluate
 */
class DisjunctionFilter<T>(private val filters: Iterable<Filter<T, out Any>>) : Filter<T, Unit>(),
    Iterable<Filter<T, out Any>> {
    /**
     * Alternative constructor with variable arguments
     */
    constructor(vararg filters: Filter<T, out Any>) : this(filters.toList())

    override fun evaluate(value: T): FilterResult<Unit> = filters.map { it.evaluate(value) }.let { result ->
        if (result.any { it is FilterResult.Match }) FilterResult.Match(Unit)
        else if (result.all { it is FilterResult.Skip }) FilterResult.Skip()
        else FilterResult.Mismatch()
    }

    override fun iterator(): Iterator<Filter<T, out Any>> = filters.iterator()

    override fun toString(): String = javaClass.name
}
