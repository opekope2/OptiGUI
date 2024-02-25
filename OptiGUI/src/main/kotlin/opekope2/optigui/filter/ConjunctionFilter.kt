package opekope2.optigui.filter

/**
 * A filter, which applies the logical AND operation between the given filters and returns the result.
 * Only skips if all sub-filters skip, and only yields match if no sub-filters yield mismatch.
 *
 * @param T The type the filter accepts
 * @param filters The sub-filters to evaluate
 */
class ConjunctionFilter<T>(private val filters: Collection<IFilter<T, *>>) : IFilter<T, Unit>, Iterable<IFilter<T, *>> {
    /**
     * Alternative constructor with variable arguments
     *
     * @param filters The sub-filters to evaluate
     */
    constructor(vararg filters: IFilter<T, *>) : this(filters.toList())

    override fun evaluate(value: T): IFilter.Result<out Unit> = filters.map { it.evaluate(value) }.let { result ->
        if (result.any { it is IFilter.Result.Mismatch }) IFilter.Result.Mismatch
        else if (result.all { it is IFilter.Result.Skip }) IFilter.Result.Skip
        else IFilter.Result.Match(Unit)
    }

    override fun iterator(): Iterator<IFilter<T, *>> = filters.iterator()

    override fun toString(): String = javaClass.name
}