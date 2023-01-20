package opekope2.filter

/**
 * A post-processor filter, which enables the output of the given sub-filter to be changed.
 *
 * Useful, when the sub-filter doesn't yield a result (like [ConjunctionFilter] or [DisjunctionFilter])
 *
 * @param T The type the filter accepts
 * @param TFilterResult The type the sub-filter's returns
 * @param TResult The type the filter returns
 * @param filter The sub-filter to evaluate
 * @param transform The function, which transforms the result of [evaluate].
 * Its input is both the input of [evaluate] and the result of [filter]
 *
 * @see PreProcessorFilter
 */
class PostProcessorFilter<T, TFilterResult, TResult>(
    private val filter: Filter<T, out TFilterResult>,
    private val transform: (input: T, result: FilterResult<out TFilterResult>) -> FilterResult<out TResult>
) : Filter<T, TResult> {
    /**
     * Creates a new post-processor filter by specifying [FilterResult.result].
     *
     * @param filter The sub-filter to evaluate
     * @param result The (constant) result of the [transform] function
     */
    constructor(filter: Filter<T, out TFilterResult>, result: TResult) : this(
        filter,
        { _, filterResult ->
            FilterResult(
                skip = filterResult.skip,
                match = filterResult.match,
                result = result
            )
        }
    )

    override fun evaluate(value: T): FilterResult<out TResult> = transform(value, filter.evaluate(value))
}
