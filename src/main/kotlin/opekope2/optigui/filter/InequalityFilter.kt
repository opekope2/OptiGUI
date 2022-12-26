package opekope2.optigui.filter

/**
 * A filter which fails when the given value is [expectedValue], succeeds otherwise, and never skips
 * @param T The type the filter accepts
 * @param expectedValue The value the filter should fail for
 */
class InequalityFilter<T>(private val expectedValue: T) : Filter<T>() {
    override fun test(value: T) = FilterResult.create<T>(value != expectedValue)
}
