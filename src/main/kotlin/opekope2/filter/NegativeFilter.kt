package opekope2.filter

/**
 * A filter, which returns the opposite result of the provided filter
 *
 * @param T the type the filter accepts
 * @param filter the filter to negate
 */
class NegativeFilter<T>(private val filter: Filter<T, out Any>) : Filter<T, Unit>() {
    override fun test(value: T) = filter.test(value).let { FilterResult<Unit>(skip = it.skip, match = !it.match) }
}
