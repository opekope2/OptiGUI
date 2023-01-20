package opekope2.filter

/**
 * A filter, which fails when the given value is [expectedValue], succeeds otherwise, and never skips.
 *
 * @param T The type the filter accepts
 * @param expectedValue The value the filter should fail for
 */
class InequalityFilter<T>(private val expectedValue: T) : Filter<T, Unit> {
    override fun evaluate(value: T): FilterResult<out Unit> =
        if (value != expectedValue) FilterResult.Match(Unit) else FilterResult.Mismatch()
}
