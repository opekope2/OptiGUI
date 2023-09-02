package opekope2.optigui.filter;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a filter result.
 *
 * @param <T> The type a {@link Filter} returns
 */
public abstract sealed class FilterResult<T> {
    @SuppressWarnings("unchecked")
    public <TThis extends FilterResult<TNewResult>, TNewResult> TThis withResult(@NotNull TNewResult result) {
        return (TThis) (this instanceof FilterResult.Match<T> ? match(result) : this);
    }

    /**
     * Returns an instance of {@link Match}, with its {@link Match#getResult() result} set to the given result.
     *
     * @param result The result to pass to the created {@link Match} result
     * @param <T>    The type of the result
     */
    public static <T> Match<T> match(@NotNull T result) {
        return new Match<>(result);
    }

    /**
     * Returns an instance of {@link Mismatch}
     */
    @SuppressWarnings("unchecked")
    public static <T> Mismatch<T> mismatch() {
        return (Mismatch<T>) Mismatch.INSTANCE;
    }

    /**
     * Returns an instance of {@link Skip}
     */
    @SuppressWarnings("unchecked")
    public static <T> Skip<T> skip() {
        return (Skip<T>) Skip.INSTANCE;
    }

    /**
     * Represents a skipping filter result.
     *
     * @param <T> The type a {@link Filter} would return in case of a match
     */
    public static final class Skip<T> extends FilterResult<T> {
        private static final Skip<?> INSTANCE = new Skip<>();

        private Skip() {
        }

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
        private static final Mismatch<?> INSTANCE = new Mismatch<>();

        private Mismatch() {
        }

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
        @NotNull
        private final T result;

        /**
         * Creates a new matching filter result.
         *
         * @param result The result of the filter
         */
        private Match(@NotNull T result) {
            this.result = result;
        }

        /**
         * Gets the result of the filter.
         */
        @NotNull
        public T getResult() {
            return result;
        }

        @Override
        public String toString() {
            return "Match, result: %s".formatted(result);
        }
    }
}
