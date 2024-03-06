package opekope2.optigui.filter

/**
 * A filter, which returns `null`, if [filter] returns a non-null value, and returns a non-null value, if [filter]
 * returns `null`.
 *
 * @param TInput The type the filter accepts
 * @param filter The filter to negate
 */
class NegationFilter<TInput>(private val filter: IFilter<TInput, *>) :
    IFilter<TInput, Unit>, Iterable<IFilter<TInput, *>> {
    override fun evaluate(input: TInput) =
        if (filter.evaluate(input) == null) Unit
        else null

    override fun iterator() = iterator { yield(filter) }

    override fun toString(): String = javaClass.name
}
