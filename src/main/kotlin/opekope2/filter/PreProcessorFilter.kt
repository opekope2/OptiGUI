package opekope2.filter

/**
 * A pre-processor filter, which applies a transform to the value before evaluating the sub-filter on it.
 *
 * @param TSource The type this filter accepts
 * @param TFilter The type the other filter accepts
 * @param TResult the type the filter returns
 * @param transform The transform to apply to the value in [evaluate] before evaluating [filter]
 * @param filter The sub-filter to evaluate
 *
 * @see PostProcessorFilter
 */
class PreProcessorFilter<TSource, TFilter, TResult>(
    private val transform: (TSource) -> TFilter,
    private val filter: Filter<TFilter, TResult>
) : Filter<TSource, TResult> {
    override fun evaluate(value: TSource) = filter.evaluate(transform(value))
}
