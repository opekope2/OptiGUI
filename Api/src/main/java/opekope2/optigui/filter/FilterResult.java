package opekope2.optigui.filter;

/**
 * Represents a filter result.
 *
 * @param <T> The type a {@link Filter} returns
 */
public abstract sealed class FilterResult<T> {
    /**
     * Represents a skipping filter result.
     *
     * @param <T> The type a {@link Filter} would return in case of a match
     */
    public static final class Skip<T> extends FilterResult<T> {
        @Override
        public String toString() {
            return "Skip";
        }
    }

    /**
     * Represents a mismatching filter result.
     *
     * @param <T> The type a {@link Filter} would return in case of a match
     */
    public static final class Mismatch<T> extends FilterResult<T> {
        @Override
        public String toString() {
            return "Mismatch";
        }
    }

    /**
     * Represents a matching filter result.
     *
     * @param <T> The type a {@link Filter} returns
     */
    public static final class Match<T> extends FilterResult<T> {
        private final T result;

        /**
         * Creates a new matching filter result.
         *
         * @param result The result of the filter
         */
        public Match(T result) {
            this.result = result;
        }

        /**
         * Gets the result of the filter.
         */
        public T getResult() {
            return result;
        }

        @Override
        public String toString() {
            return "Match, result: %s".formatted(result);
        }
    }
}
