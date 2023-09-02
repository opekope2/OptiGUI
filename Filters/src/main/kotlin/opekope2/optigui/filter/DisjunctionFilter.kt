package opekope2.optigui.filter

/**
 * A filter, which applies the logical OR operation between the given filters and returns the result.
 * Only skips if all sub-filters skip, and only yields mismatch if no sub-filters yield match.
 *
 * @param T The type the filter accepts
 * @param filters The sub-filters to evaluate
 */
class DisjunctionFilter<T>(private val filters: Iterable<IFilter<T, *>>) : IFilter<T, Unit>, Iterable<IFilter<T, *>> {
    /**
     * Alternative constructor with variable arguments
     */
    constructor(vararg filters: IFilter<T, *>) : this(filters.toList())

    override fun evaluate(value: T): IFilter.Result<Unit> = filters.map { it.evaluate(value) }.let { result ->
        if (result.any { it is IFilter.Result.Match }) IFilter.Result.match(Unit)
        else if (result.all { it is IFilter.Result.Skip }) IFilter.Result.skip()
        else IFilter.Result.mismatch()
    }

    override fun iterator(): Iterator<IFilter<T, *>> = filters.iterator()

    override fun toString(): String = javaClass.name
}
