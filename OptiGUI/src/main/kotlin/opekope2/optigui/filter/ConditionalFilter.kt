package opekope2.optigui.filter

import java.util.*

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
    private val falseResult: IFilter.Result<TResult>,
    private val filter: IFilter<T, TResult>
) : IFilter<T, TResult>, Iterable<IFilter<T, TResult>> {
    override fun evaluate(value: T): IFilter.Result<out TResult> =
        if (check(value)) filter.evaluate(value)
        else falseResult

    override fun iterator(): Iterator<IFilter<T, TResult>> = setOf(filter).iterator()

    override fun toString(): String = "${javaClass.name}, result if check is false: $falseResult"

    companion object {
        /**
         * Creates a [ConditionalFilter], which gets an [Optional]'s value when passing to [filter] if
         * [present][Optional.isPresent].
         *
         * @param T The type the given [filter] accepts
         * @param TResult The type [filter] returns
         * @param notPresentResult The result when the input is not present
         * @param filter The filter to evaluate
         */
        fun <T, TResult> optional(
            notPresentResult: IFilter.Result<TResult>,
            filter: IFilter<T, TResult>
        ): ConditionalFilter<Optional<T>, TResult> = ConditionalFilter(
            { it.isPresent },
            notPresentResult,
            PreProcessorFilter(
                { it.get() },
                "Get optional value",
                filter
            )
        )
    }
}
