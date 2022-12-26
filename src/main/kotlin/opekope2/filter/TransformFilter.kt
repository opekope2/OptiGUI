package opekope2.filter

import opekope2.util.convert

/**
 * A filter which applies a transform to the value before evaluating another filter on it
 *
 * @param TSource The type this filter accepts
 * @param TFilter The type the other filter accepts
 * @param transform The transform to apply to the value in [test] before evaluating [filter]
 * @param filter The other filter
 * @param replacementConverter The optional converter, which converts the result of [filter] back to [TSource]
 */
class TransformFilter<TSource, TFilter> @JvmOverloads constructor(
    private val transform: (TSource) -> TFilter,
    private val filter: Filter<TFilter>,
    private val replacementConverter: ((TFilter) -> TSource)? = null
) : Filter<TSource>() {
    override fun test(value: TSource) = filter.test(transform(value)).convert(replacementConverter)
}
