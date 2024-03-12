package opekope2.optigui.filter

/**
 * A filter, which applies the logical AND operation between the given [filters], and returns a non-null value, if all
 * the sub-filters returned a non-null value, or `null`, if any of the sub-filters returned `null`.
 *
 * @param TInput The type the filter accepts
 * @param filters The sub-filters to evaluate
 */
class ConjunctionFilter<TInput>(private val filters: Collection<IFilter<TInput, *>>) :
    IFilter<TInput, Unit>, Iterable<IFilter<TInput, *>> {
    /**
     * Alternative constructor with variable arguments
     *
     * @param filters The sub-filters to evaluate
     */
    constructor(vararg filters: IFilter<TInput, *>) : this(filters.toList())

    override fun evaluate(input: TInput): Unit? {
        for (filter in filters) {
            if (filter.evaluate(input) == null) return null
        }
        return Unit
    }

    override fun iterator() = filters.iterator()

    override fun toString(): String = javaClass.name
}
