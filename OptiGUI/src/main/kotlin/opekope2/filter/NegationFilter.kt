package opekope2.filter

/**
 * A filter, which returns the opposite result of the provided filter.
 *
 * [FilterResult.Match] and [FilterResult.Mismatch] are opposites.
 * [FilterResult.Skip] is the opposite of itself.
 *
 * @param T The type the filter accepts
 * @param filter The filter to negate
 */
class NegationFilter<T>(private val filter: Filter<T, out Any>) : Filter<T, Unit>, Iterable<Filter<T, out Any>> {
    override fun evaluate(value: T): FilterResult<out Unit> = when (filter.evaluate(value)) {
        is FilterResult.Match -> FilterResult.Mismatch()
        is FilterResult.Mismatch -> FilterResult.Match(Unit)
        is FilterResult.Skip -> FilterResult.Skip()
    }

    override fun iterator(): Iterator<Filter<T, out Any>> = setOf(filter).iterator()

    override fun toString(): String = javaClass.name
}
