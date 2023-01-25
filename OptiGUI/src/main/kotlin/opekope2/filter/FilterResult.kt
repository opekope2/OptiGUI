package opekope2.filter

/**
 * Represents a filter result.
 *
 * @param T The type a [Filter] returns
 */
sealed interface FilterResult<T> {
    /**
     * Represents a skipping filter result.
     *
     * @param T The type a [Filter] would return in case of a match
     */
    class Skip<T> : FilterResult<T>

    /**
     * Represents a mismatching filter result.
     *
     * @param T The type a [Filter] would return in case of a match
     */
    class Mismatch<T> : FilterResult<T>

    /**
     * Represents a matching filter result.
     *
     * @param T The type a [Filter] returns
     * @param result The result of the filter
     */
    data class Match<T>(val result: T) : FilterResult<T>
}
