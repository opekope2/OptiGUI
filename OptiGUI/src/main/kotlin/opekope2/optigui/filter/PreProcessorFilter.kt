package opekope2.optigui.filter

/**
 * A pre-processor filter, which applies a [transform] to the value before evaluating the sub-filter on it.
 *
 * @param TInput The type this filter accepts
 * @param TSubFilterInput The type the sub-filter accepts
 * @param TResult the type the filter returns
 * @param transform The transform to apply to the value in [evaluate] before evaluating [filter]
 * @param transformDescription Textual description of [transform] for better [dump] readability
 * @param filter The sub-filter to evaluate
 *
 * @see PostProcessorFilter
 * @see InputNullGuardFilter
 */
class PreProcessorFilter<TInput, TSubFilterInput, TResult : Any>(
    private val transform: (TInput) -> TSubFilterInput,
    private val transformDescription: String,
    private val filter: IFilter<TSubFilterInput, TResult>
) : IFilter<TInput, TResult>, Iterable<IFilter<TSubFilterInput, TResult>> {
    override fun evaluate(input: TInput) = filter.evaluate(transform(input))

    override fun iterator() = iterator { yield(filter) }

    override fun toString() = "${javaClass.name}, transform: $transformDescription"

    companion object {
        /**
         * Creates a [PreProcessorFilter], which doesn't pass null as an input to [filter].
         *
         * @param TInput The type [PreProcessorFilter] filter accepts
         * @param TSubFilterInput The type [filter] accepts
         * @param TResult the type [filter] returns
         * @param transform The transform to pass to [PreProcessorFilter.transform]
         * @param transformDescription Textual description of [transform] for better [dump] readability
         * @param inputNullResult The result to pass to [InputNullGuardFilter.inputNullResult]
         * @param filter The sub-filter to evaluate
         */
        @JvmStatic
        fun <TInput, TSubFilterInput, TResult : Any> nullGuarded(
            transform: (TInput) -> TSubFilterInput?,
            transformDescription: String,
            inputNullResult: TResult?,
            filter: IFilter<TSubFilterInput, TResult>
        ) = PreProcessorFilter(transform, transformDescription, InputNullGuardFilter(inputNullResult, filter))
    }
}
