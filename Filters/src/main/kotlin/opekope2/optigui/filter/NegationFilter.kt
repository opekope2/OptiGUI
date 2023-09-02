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
        when (it) {
            is FilterResult.Match -> FilterResult.Mismatch()
            is FilterResult.Mismatch -> FilterResult.Match(Unit)
            is FilterResult.Skip -> FilterResult.Skip()
            else -> throw RuntimeException("Invalid filter result: `${it.javaClass}`") // Java moment
        }
    }

    override fun iterator(): Iterator<Filter<T, out Any>> = setOf(filter).iterator()

    override fun toString(): String = javaClass.name
}
