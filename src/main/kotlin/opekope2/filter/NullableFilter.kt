package opekope2.filter

/**
 * A filter, which forwards evaluation to the given [filter], unless the input is `null`;
 * in this case, it can skip, produce a match or mismatch result.
 *
 * @param T The type the given [filter] accepts. This filter accepts its nullable form
 * @param TResult The type [filter] returns
 * @param skipOnNull `true` if skip when the input is null, `false` otherwise
 * @param failOnNull `true` to yield a mismatch result, `false` o yield a match result
 * @param filter The filter to evaluate
 */
class NullableFilter<T, TResult>(
    val skipOnNull: Boolean,
    val failOnNull: Boolean,
    val filter: Filter<T, TResult>
) : Filter<T?, TResult>() {
    override fun test(value: T?): FilterResult<out TResult> =
        if (value == null) FilterResult(skip = skipOnNull, match = !failOnNull)
        else filter.test(value)
}
