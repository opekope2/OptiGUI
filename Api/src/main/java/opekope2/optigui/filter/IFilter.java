package opekope2.optigui.filter;

import opekope2.lilac.util.Tree;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * Base class for filtering.
 * <br>
 * If the filter evaluates sub-filters, it should implement {@link Iterable} to show them in the dumped tree.
 *
 * @param <T>       The type the filter accepts
 * @param <TResult> The type the filter returns
 */
public interface IFilter<T, TResult> {
    /**
     * Evaluates the filter with the given value.
     * <br>
     * OptiGUI expects all filters to be deterministic (i.e. returns the same output for the same input).
     *
     * @param value The value the filter should evaluate
     * @return The result of the filter
     */
    @NotNull IFilter.Result<? extends TResult> evaluate(/* I didn't forget to annotate */ T value);

    @NotNull
    default Tree.Node createTree() {
        BiConsumer<Iterable<?>, Tree.Node> iterableTreeFactory = new BiConsumer<>() {
            @Override
            public void accept(Iterable<?> iterable, Tree.Node parent) {
                for (Object next : iterable) {
                    Tree.Node child = parent.appendChild(next.toString());

                    if (next instanceof Iterable<?>) {
                        accept((Iterable<?>) next, child);
                    }
                }
            }
        };

        Tree.Node node = new Tree.Node(toString());
        if (this instanceof Iterable<?>) {
            iterableTreeFactory.accept((Iterable<?>) this, node);
        }
        return node;
    }

    /**
     * Represents a filter result.
     *
     * @param <T> The type a {@link IFilter filter} returns
     */
    abstract sealed class Result<T> {
        @SuppressWarnings("unchecked")
        public <TThis extends Result<TNewResult>, TNewResult> TThis withResult(@NotNull TNewResult result) {
            return (TThis) (this instanceof Result.Match<T> ? match(result) : this);
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
         * @param <T> The type a {@link IFilter filter} would return in case of a match
         */
        public static final class Skip<T> extends Result<T> {
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
         * @param <T> The type a {@link IFilter filter} would return in case of a match
         */
        public static final class Mismatch<T> extends Result<T> {
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
         * @param <T> The type a {@link IFilter filter} returns
         */
        public static final class Match<T> extends Result<T> {
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
}
