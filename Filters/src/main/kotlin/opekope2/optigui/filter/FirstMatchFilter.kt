package opekope2.optigui.filter

/**
 * A filter, which returns the first [IFilter.Result.Match] result from [filters].
 * Only skips if all sub-filters skip, and only yields mismatch if no sub-filters yield match.
 *
 * @param T The type the filter accepts
 * @param TResult The type the filter returns
 * @param filters The sub-filters to evaluate
 */
class FirstMatchFilter<T, TResult : Any>(private val filters: Iterable<IFilter<T, out TResult>>) :
    IFilter<T, TResult>, Iterable<IFilter<T, out TResult>> {
    /**
     * Alternative constructor with variable arguments
     */
    constructor(vararg filters: IFilter<T, out TResult>) : this(filters.toList())

    override fun evaluate(value: T): IFilter.Result<out TResult> = filters.map { it.evaluate(value) }.let { results ->
        if (results.all { it is IFilter.Result.Skip }) IFilter.Result.skip()
        else results.firstOrNull { it is IFilter.Result.Match } ?: IFilter.Result.mismatch()
    }

    override fun iterator(): Iterator<IFilter<T, out TResult>> = filters.iterator()

    override fun toString(): String = javaClass.name
}
