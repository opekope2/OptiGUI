package opekope2.optigui.filter

/**
 * Base class for all filters.
 *
 * @param T The type the filter accepts
 */
abstract class Filter<T> {
    /**
     * Evaluates the filter with the given value.
     *
     * @param value The value the filter should evaluate
     * @return The result of the filter, which optionally includes a replacement
     */
    abstract fun test(value: T): FilterResult<out T>
}
