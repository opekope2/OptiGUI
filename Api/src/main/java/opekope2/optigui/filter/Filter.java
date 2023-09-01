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
public abstract class Filter<T, TResult> {
    /**
     * Evaluates the filter with the given value.
     * <br>
     * OptiGUI expects all filters to be deterministic (i.e. returns the same output for the same input).
     *
     * @param value The value the filter should evaluate
     * @return The result of the filter
     */
    @NotNull
    public abstract FilterResult<? extends TResult> evaluate(/* I didn't forget to annotate */ T value);

    @NotNull
    public final Tree.Node createTree() {
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
}
