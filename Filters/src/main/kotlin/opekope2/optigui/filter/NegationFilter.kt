package opekope2.optigui.filter

/**
 * A filter, which returns the opposite result of the provided filter.
 *
 * [FilterResult.Match] and [FilterResult.Mismatch] are opposites.
 * [FilterResult.Skip] is the opposite of itself.
 *
 * @param T The type the filter accepts
 * @param filter The filter to negate
 */
class NegationFilter<T>(private val filter: Filter<T, *>) : Filter<T, Unit>(), Iterable<Filter<T, *>> {
    override fun evaluate(value: T): FilterResult<out Unit> = filter.evaluate(value).let {
        @Suppress("UNCHECKED_CAST")
        when (it) {
            is FilterResult.Match -> FilterResult.mismatch()
            is FilterResult.Mismatch -> FilterResult.match(Unit)
            else -> it as FilterResult<Unit>
        }
    }

    override fun iterator(): Iterator<Filter<T, out Any>> = setOf(filter).iterator()

    override fun toString(): String = javaClass.name
}
