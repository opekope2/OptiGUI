package opekope2.optigui.filter

/**
 * A filter, which forwards evaluation to the given [filter],
 * if the [check] function returns `true`. Otherwise, the result will be [falseResult].
 *
 * @param T The type the given [filter] accepts
 * @param TResult The type [filter] returns
 * @param check The function to decide if the evaluation should be forwarded to [filter] or return [falseResult]
 * @param falseResult The result when the input is `false`
 * @param filter The filter to evaluate
 */
class ConditionalFilter<T, TResult>(
    private val check: (T) -> Boolean,
    private val falseResult: FilterResult<TResult>,
    private val filter: Filter<T, TResult>
) : Filter<T, TResult>(), Iterable<Filter<T, TResult>> {
    override fun evaluate(value: T): FilterResult<out TResult> =
        if (check(value)) filter.evaluate(value)
        else falseResult

    override fun iterator(): Iterator<Filter<T, TResult>> = setOf(filter).iterator()

    override fun toString(): String = "${javaClass.name}, result if check is false: $falseResult"
}
