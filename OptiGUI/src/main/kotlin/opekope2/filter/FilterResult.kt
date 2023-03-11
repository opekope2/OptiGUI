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
    class Skip<T> : FilterResult<T> {
        override fun toString(): String = "Skip"
    }

    /**
     * Represents a mismatching filter result.
     *
     * @param T The type a [Filter] would return in case of a match
     */
    class Mismatch<T> : FilterResult<T> {
        override fun toString(): String = "Mismatch"
    }

    /**
     * Represents a matching filter result.
     *
     * @param T The type a [Filter] returns
     * @param result The result of the filter
     */
    class Match<T>(val result: T) : FilterResult<T> {
        override fun toString(): String = "Match, result: $result"
    }
}
