package opekope2.filter

/**
 * Base class for all filters.
 *
 * @param T The type the filter accepts
 * @param TResult the type the filter returns
 */
abstract class Filter<T, TResult> {
    /**
     * Evaluates the filter with the given value.
     *
     * @param value The value the filter should evaluate
     * @return The result of the filter, which optionally includes a replacement
     */
    abstract fun test(value: T): FilterResult<out TResult>
}
