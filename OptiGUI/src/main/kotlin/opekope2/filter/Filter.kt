package opekope2.filter

/**
 * Functional interface for filtering.
 *
 * This interface can be used as a functional interface, but when dumped with [opekope2.util.dump], it will be ugly.
 *
 * If the filter evaluates sub-filters, please implement [Iterable] to show them in the dumped tree.
 *
 * @param T The type the filter accepts
 * @param TResult The type the filter returns
 */
fun interface Filter<T, TResult> {
    /**
     * Evaluates the filter with the given value.
     *
     * @param value The value the filter should evaluate
     * @return The result of the filter, which optionally includes a replacement
     */
    fun evaluate(value: T): FilterResult<out TResult>
}
