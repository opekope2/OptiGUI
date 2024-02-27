package opekope2.optigui.filter

import opekope2.optigui.util.TreeFormatter

/**
 * Functional interface for filtering.
 *
 * When used as a functional interface, it will be ugly when dumped with [dump].
 * If the filter evaluates sub-filters, implement [Iterable] to show them in the dumped tree.
 *
 * @param T The type the filter accepts
 * @param TResult The type the filter returns
 */
fun interface IFilter<T, TResult> {
    /**
     * Evaluates the filter with the given value.
     *
     * OptiGUI expects all filters to be deterministic (i.e. returns the same output for the same input)
     *
     * @param value The value the filter should evaluate
     * @return The result of the filter, which optionally includes a replacement
     */
    fun evaluate(value: T): Result<out TResult>

    /**
     * Formats the current filter as a tree with ASCII characters, and returns the formatted string.
     */
    fun dump(): String {
        return dump(TreeFormatter(), last = true)
    }

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

    /**
     * Represents a filter result.
     *
     * @param T The type an [IFilter] returns
     */
    sealed class Result<T> {
        /**
         * If the current [IFilter.Result] is [IFilter.Result.Match], change its result to [result].
         * Otherwise, return the original.
         *
         * @param TResult The type of the new result
         * @param result The new result
         */
        @Suppress("UNCHECKED_CAST")
        fun <TResult> withResult(result: TResult) =
            if (this is Match<*>) Match(result)
            else this as Result<TResult>

        /**
         * Represents a matching filter result.
         *
         * @param T The type an [IFilter] returns
         * @param result The result of the filter
         */
        data class Match<T>(val result: T) : Result<T>() {
            override fun toString() = "Match, result: $result"
        }

        /**
         * Represents a mismatching filter result.
         */
        data object Mismatch : Result<Nothing>() {
            override fun toString() = "Mismatch"
        }

        /**
         * Represents a skipping filter result.
         */
        data object Skip : Result<Nothing>() {
            override fun toString() = "Skip"
        }

        @Suppress("UNCHECKED_CAST")
        companion object {
            /**
             * Returns [Mismatch] with its generic parameter cast to [T].
             *
             * @param T The type an [IFilter] returns
             */
            @JvmStatic
            fun <T> mismatch(): Result<T> = Mismatch as Result<T>

            /**
             * Returns [Skip] with its generic parameter cast to [T].
             *
             * @param T The type an [IFilter] returns
             */
            @JvmStatic
            fun <T> skip(): Result<T> = Skip as Result<T>
        }
    }
}
