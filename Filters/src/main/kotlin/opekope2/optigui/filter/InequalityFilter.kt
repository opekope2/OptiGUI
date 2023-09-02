package opekope2.optigui.filter

/**
 * A filter, which fails when the given value is [unexpectedValue], succeeds otherwise, and never skips.
 *
 * @param T The type the filter accepts
 * @param unexpectedValue The value the filter should fail for
 */
class InequalityFilter<T>(private val unexpectedValue: T) : IFilter<T, Unit> {
    override fun evaluate(value: T): IFilter.Result<out Unit> =
        if (value != unexpectedValue) IFilter.Result.match(Unit) else IFilter.Result.mismatch()

    override fun toString(): String = "${javaClass.name}, unexpected value: $unexpectedValue"
}
