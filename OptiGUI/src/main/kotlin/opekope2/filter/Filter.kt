package opekope2.filter

/**
 * Functional interface for filtering.
 *
 * When used as a functional interface, it will be ugly when dumped with [opekope2.util.dump].
 * If the filter evaluates sub-filters, implement [Iterable] to show them in the dumped tree.
 *
 * @param T The type the filter accepts
 * @param TResult The type the filter returns
 */
fun interface Filter<T, TResult> {
    /**
     * Evaluates the filter with the given value.
     *
     * OptiGUI expects all filters to be deterministic (i.e. returns the same output for the same input)
     *
     * @param value The value the filter should evaluate
     * @return The result of the filter, which optionally includes a replacement
     */
    fun evaluate(value: T): FilterResult<out TResult>
}
