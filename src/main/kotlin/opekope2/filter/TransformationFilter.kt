package opekope2.filter

/**
 * A filter which applies a transform to the value before evaluating another filter on it.
 *
 * @param TSource The type this filter accepts
 * @param TFilter The type the other filter accepts
 * @param TResult the type the filter returns
 * @param transform The transform to apply to the value in [evaluate] before evaluating [filter]
 * @param filter The other filter
 */
class TransformationFilter<TSource, TFilter, TResult>(
    private val transform: (TSource) -> TFilter,
    private val filter: Filter<TFilter, TResult>
) : Filter<TSource, TResult> {
    override fun evaluate(value: TSource) = filter.evaluate(transform(value))
}
