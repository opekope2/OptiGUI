package opekope2.filter

/**
 * A filter, which returns the opposite result of the provided filter.
 *
 * @param T The type the filter accepts
 * @param filter The filter to negate
 */
class NegationFilter<T>(private val filter: Filter<T, out Any>) : Filter<T, Unit> {
    override fun evaluate(value: T): FilterResult<out Unit> = when (filter.evaluate(value)) {
        is FilterResult.Skip -> FilterResult.Skip()
        is FilterResult.Mismatch -> FilterResult.Match(Unit)
        is FilterResult.Match -> FilterResult.Mismatch()
    }
}
