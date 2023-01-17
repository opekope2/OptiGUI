package opekope2.filter

/**
 * A filter, which returns the opposite result of the provided filter.
 *
 * @param T the type the filter accepts
 * @param filter the filter to negate
 */
class NegationFilter<T>(private val filter: Filter<T, out Any>) : Filter<T, Unit>() {
    override fun evaluate(value: T): FilterResult<out Unit> = filter.evaluate(value).let { FilterResult(skip = it.skip, match = !it.match) }
}
