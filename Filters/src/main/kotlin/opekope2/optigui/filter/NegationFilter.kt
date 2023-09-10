package opekope2.optigui.filter

/**
 * A filter, which returns the opposite result of the provided filter.
 *
 * [IFilter.Result.Match] and [IFilter.Result.Mismatch] are opposites.
 * [IFilter.Result.Skip] is the opposite of itself.
 *
 * @param T The type the filter accepts
 * @param filter The filter to negate
 */
class NegationFilter<T>(private val filter: IFilter<T, *>) : IFilter<T, Unit>, Iterable<IFilter<T, *>> {
    override fun evaluate(value: T): IFilter.Result<out Unit> = filter.evaluate(value).let {
        @Suppress("UNCHECKED_CAST")
        when (it) {
            is IFilter.Result.Match -> IFilter.Result.mismatch()
            is IFilter.Result.Mismatch -> IFilter.Result.match(Unit)
            else -> it as IFilter.Result<Unit>
        }
    }

    override fun iterator(): Iterator<IFilter<T, out Any>> = setOf(filter).iterator()

    override fun toString(): String = javaClass.name
}
