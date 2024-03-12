package opekope2.optigui.filter

/**
 * A post-processor filter, which changes the output of the given sub-filter.
 *
 * @param TInput The type the filter accepts
 * @param TSubFilterResult The type the sub-filter's returns
 * @param TResult The type the filter returns
 * @param filter The sub-filter to evaluate
 * @param transformDescription Textual description of [transform] for better [dump] readability
 * @param transform The function, which transforms the result of [evaluate].
 * Its input is both the input of [evaluate] and the result of [filter]
 *
 * @see PreProcessorFilter
 */
class PostProcessorFilter<TInput, TSubFilterResult : Any, TResult : Any>(
    private val filter: IFilter<TInput, out TSubFilterResult>,
    private val transformDescription: String,
    private val transform: (input: TInput, result: TSubFilterResult?) -> TResult?
) : IFilter<TInput, TResult>, Iterable<IFilter<TInput, out TSubFilterResult>> {
    /**
     * Creates a new post-processor filter by specifying its return value.
     *
     * @param filter The sub-filter to evaluate
     * @param result The (constant) return value of the [transform] function
     */
    constructor(filter: IFilter<TInput, out TSubFilterResult>, result: TResult) : this(
        filter,
        "Set result to `$result`",
        { _, subFilterResult -> result.takeIf { subFilterResult != null } }
    )

    override fun evaluate(input: TInput) = transform(input, filter.evaluate(input))

    override fun iterator() = iterator { yield(filter) }

    override fun toString() = "${javaClass.name}, transform: $transformDescription"
}
