package opekope2.filter

/**
 * Represents a filter result.
 *
 * @param T The type a [Filter] yields if the result is a match
 * @param skip Whether the result indicates that it should be skipped
 * @param match `true` if the result is a match, `false` if it's a mismatch
 * @param result The optional result
 */
class FilterResult<T> @JvmOverloads constructor(skip: Boolean, match: Boolean = false, result: T? = null) {
    /**
     * Whether the result is skipped (the filter was unable to process).
     */
    var skip = false
        private set

    /**
     * `true` if the result is a match, `false`, if it's a mismatch.
     * `false`, if [skip] is `true` (and doesn't have a meaning).
     */
    var match = false
        private set

    /**
     * The optional result.
     * `null`, if [skip] is `true` or [match] is `false` (and doesn't have a meaning).
     */
    var result: T? = null
        private set

    init {
        this.skip = skip
        if (!skip) {
            this.match = match
            if (match) {
                this.result = result
            }
        }
    }
}
