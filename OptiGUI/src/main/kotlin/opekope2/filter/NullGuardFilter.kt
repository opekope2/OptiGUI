package opekope2.filter

/**
 * A filter, which forwards evaluation to the given [filter], unless the input is `null`;
 * in this case, the result will be [nullResult].
 *
 * @param T The type the given [filter] accepts. This filter accepts its nullable form
 * @param TResult The type [filter] returns
 * @param nullResult The result when the input is null
 * @param filter The filter to evaluate
 */
class NullGuardFilter<T, TResult>(
    private val nullResult: FilterResult<TResult>,
    private val filter: Filter<T, TResult>
) : Filter<T?, TResult> {
    override fun evaluate(value: T?): FilterResult<out TResult> =
        if (value == null) nullResult
        else filter.evaluate(value)
}
