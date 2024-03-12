package opekope2.optigui.filter

import opekope2.optigui.util.TreeFormatter

/**
 * Functional interface for filtering.
 *
 * When used as a functional interface, it will be ugly when [dumped][dump].
 * If the filter evaluates sub-filters, implement [Iterable] to show them in the dumped tree.
 *
 * @param TInput The type the filter accepts
 * @param TResult The type the filter returns
 */
fun interface IFilter<TInput, TResult : Any> {
    /**
     * Evaluates the filter with the given value.
     *
     * All filters are assumed to be deterministic (return the same value for the same input).
     *
     * @param input The value the filter should evaluate
     */
    fun evaluate(input: TInput): TResult?

    /**
     * Formats the current filter as a tree, and returns the formatted string.
     */
    fun dump() = dump(TreeFormatter(), last = true)

    private fun dump(writer: TreeFormatter, last: Boolean): String {
        writer.indent()
        writer.append(toString(), lastChild = last)

        if (this is Iterable<*>) {
            val it = iterator()
            while (it.hasNext()) {
                val next = it.next()
                if (next is IFilter<*, *>) next.dump(writer, !it.hasNext())
                else writer.indent { append(next.toString(), lastChild = !it.hasNext()) }
            }
        }

        writer.unindent()

        return writer.toString()
    }
}
