package opekope2.optigui.filter

/**
 * A filter, which returns the first non-null evaluation from [filters].
 * It returns `null`, if none of [filters] return a non-null value, or [filters] is empty.
 *
 * @param TInput The type the filter accepts
 * @param TResult The type the filter returns
 * @param filters The sub-filters to evaluate
 */
open class FirstMatchFilter<TInput, TResult : Any>(private val filters: Collection<IFilter<TInput, out TResult>>) :
    IFilter<TInput, TResult>, Iterable<IFilter<TInput, out TResult>> {
    /**
     * Alternative constructor with variable arguments
     *
     * @param filters The sub-filters to evaluate
     */
    constructor(vararg filters: IFilter<TInput, out TResult>) : this(filters.toList())

    override fun evaluate(input: TInput): TResult? {
        for (filter in filters) {
            val result = filter.evaluate(input)
            if (result != null) return result
        }
        return null
    }

    override fun iterator(): Iterator<IFilter<TInput, out TResult>> = filters.iterator()

    override fun toString(): String = javaClass.name
}
