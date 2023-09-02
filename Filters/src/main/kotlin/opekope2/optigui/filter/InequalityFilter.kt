package opekope2.optigui.filter

/**
 * A filter, which fails when the given value is [unexpectedValue], succeeds otherwise, and never skips.
 *
 * @param T The type the filter accepts
 * @param unexpectedValue The value the filter should fail for
 */
class InequalityFilter<T>(private val unexpectedValue: T) : Filter<T, Unit>() {
    override fun evaluate(value: T): FilterResult<out Unit> =
        if (value != unexpectedValue) FilterResult.match(Unit) else FilterResult.mismatch()

    override fun toString(): String = "${javaClass.name}, unexpected value: $unexpectedValue"
}
