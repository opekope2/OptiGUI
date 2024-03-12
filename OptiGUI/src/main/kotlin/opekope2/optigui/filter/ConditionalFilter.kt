package opekope2.optigui.filter

import java.util.*

/**
 * A filter, which forwards evaluation to the given [filter],
 * if the [check] function returns `true`. Otherwise, the result will be [falseResult].
 *
 * @param TInput The type the given [filter] accepts
 * @param TResult The type [filter] returns
 * @param check The function to decide if the evaluation should be forwarded to [filter] or return [falseResult]
 * @param falseResult The result when the input is `false`
 * @param filter The filter to evaluate
 */
class ConditionalFilter<TInput, TResult : Any>(
    private val check: (TInput) -> Boolean,
    private val falseResult: TResult?,
    private val filter: IFilter<TInput, TResult>
) : IFilter<TInput, TResult>, Iterable<IFilter<TInput, TResult>> {
    override fun evaluate(input: TInput) =
        if (check(input)) filter.evaluate(input)
        else falseResult

    override fun iterator() = iterator { yield(filter) }

    override fun toString() = "${javaClass.name}, result if check is false: $falseResult"

    companion object {
        /**
         * Creates a [ConditionalFilter], which gets an [Optional]'s value when passing to [filter] if
         * [present][Optional.isPresent].
         *
         * @param TInput The type the given [filter] accepts
         * @param TResult The type [filter] returns
         * @param notPresentResult The result when the input is not present
         * @param filter The filter to evaluate
         */
        fun <TInput, TResult : Any> optional(
            notPresentResult: TResult?,
            filter: IFilter<TInput, TResult>
        ) = ConditionalFilter<Optional<TInput>, TResult>(
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
