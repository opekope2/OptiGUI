package opekope2.optigui.filter

/**
 * A pre-processor filter, which applies a transform to the value before evaluating the sub-filter on it.
 * For more advanced control over invoking [filter], please use an arrow function.
 *
 * @param TSource The type this filter accepts
 * @param TFilter The type the other filter accepts
 * @param TResult the type the filter returns
 * @param transform The transform to apply to the value in [evaluate] before evaluating [filter]
 * @param filter The sub-filter to evaluate
 *
 * @see PostProcessorFilter
 * @see NullGuardFilter
 */
class PreProcessorFilter<TSource, TFilter, TResult>(
    private val transform: (TSource) -> TFilter,
    private val filter: IFilter<TFilter, TResult>
) : IFilter<TSource, TResult>, Iterable<IFilter<TFilter, TResult>> {
    override fun evaluate(value: TSource) = filter.evaluate(transform(value))

    override fun iterator(): Iterator<IFilter<TFilter, TResult>> = setOf(filter).iterator()

    override fun toString(): String = javaClass.name

    companion object {
        /**
         * Creates a [PreProcessorFilter], which doesn't pass null as an input to [filter].
         *
         * @param TSource The type [PreProcessorFilter] filter accepts
         * @param TFilter The type [filter] accepts
         * @param TResult the type [filter] returns
         * @param transform The transform to pass to [PreProcessorFilter.transform]
         * @param nullResult The result to pass to [NullGuardFilter.nullResult]
         * @param filter The sub-filter to evaluate
         */
        @JvmStatic
        fun <TSource, TFilter, TResult> nullGuarded(
            transform: (TSource) -> TFilter?,
            nullResult: IFilter.Result<TResult>,
            filter: IFilter<TFilter, TResult>
        ) = PreProcessorFilter(transform, NullGuardFilter(nullResult, filter))
    }
}
