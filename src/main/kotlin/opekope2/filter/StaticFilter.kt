package opekope2.filter

/**
 * A filter, which returns the given result no matter what the input is.
 *
 * @param T The type the filter accepts
 * @param TResult The type the filter returns
 * @param result The result [evaluate] returns
 */
class StaticFilter<T, TResult>(private var result: FilterResult<out TResult>) : Filter<T, TResult> {
    override fun evaluate(value: T): FilterResult<out TResult> = result
}
