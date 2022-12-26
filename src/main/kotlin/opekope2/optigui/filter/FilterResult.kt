package opekope2.optigui.filter

/**
 * Represents a filter result
 *
 * @param skip Whether the result indicates that it should be skipped
 * @param match True if the result is a match, false if the result is a mismatch. Ignored, if [skip] is `false`
 * @param replacement The optional replacement. Ignored, if [skip] is `false` or [match] is `false`
 */
class FilterResult<T> private constructor(
    val skip: Boolean,
    val match: Boolean = false,
    val replacement: T? = null
) {
    companion object {
        /**
         * Creates a filter result indicating a match
         * @param replacement The optional replacement
         */
        @JvmStatic
        @JvmOverloads
        fun <T> match(replacement: T? = null) = FilterResult(skip = false, match = true, replacement = replacement)

        /**
         * Creates a filter result indicating a mismatch
         */
        @JvmStatic
        fun <T> mismatch() = FilterResult<T>(skip = false, match = false)

        /**
         * Creates a filter result indicating a skip (the filter was unable to process)
         */
        @JvmStatic
        fun <T> skip() = FilterResult<T>(skip = true)

        /**
         * Creates a filter result, indicating a match or mismatch (which was not skipped)
         *
         * @param match Whether the filter was matched
         * @param replacement The optional replacement
         */
        @JvmStatic
        @JvmOverloads
        fun <T> create(match: Boolean, replacement: T? = null) = if (match) match(replacement) else mismatch()
    }
}
