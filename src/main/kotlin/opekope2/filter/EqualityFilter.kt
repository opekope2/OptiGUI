package opekope2.filter

/**
 * A filter which succeeds when the given value is [expectedValue], fails otherwise, and never skips.
 *
 * @param T The type the filter accepts
 * @param expectedValue The value the filter should succeed for
 */
class EqualityFilter<T>(private val expectedValue: T) : Filter<T>() {
    override fun test(value: T) = FilterResult<T>(skip = false, match = value == expectedValue)
}
