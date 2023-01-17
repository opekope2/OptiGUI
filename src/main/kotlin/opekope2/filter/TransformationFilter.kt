package opekope2.filter

/**
 * A filter, which applies a transform to the value before evaluating another filter on it. If the transform returns `null`,
 * the other filter doesn't get evaluated, and the behavior can be specified with [skipOnNull] and [mismatchOnNull].
 *
 * @param TSource The type this filter accepts
 * @param TFilter The type the other filter accepts, which will not be `null` when [filter] is evaluated
 * @param TResult The type the other filter returns
 * @param transform The transform to apply to the value in [evaluate] before evaluating [filter]
 * @param skipOnNull When [transform] returns `null`: `true` to skip, `false` to use [mismatchOnNull] setting
 * @param mismatchOnNull When [transform] returns `null`: `true` to yield a mismatch result, `false` to yield a match result
 * @param filter The other filter
 *
 * @see NullableTransformationFilter
 */
class TransformationFilter<TSource, TFilter : Any, TResult>(
    private val transform: (TSource) -> TFilter?,
    private val skipOnNull: Boolean,
    private val mismatchOnNull: Boolean,
    private val filter: Filter<TFilter, TResult>
) : Filter<TSource, TResult>() {
    override fun evaluate(value: TSource): FilterResult<out TResult> =
        transform(value)?.let(filter::evaluate) ?: FilterResult(skip = skipOnNull, match = !mismatchOnNull)
}
