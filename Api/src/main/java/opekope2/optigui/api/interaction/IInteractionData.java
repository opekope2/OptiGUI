package opekope2.optigui.api.interaction;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;


/**
 * Represents {@link Interaction#data()}, which can be exported to selectors.
 */
public interface IInteractionData {
    /**
     * Writes the selectors of the current object to the given function.
     *
     * @param appendSelector The function accepting the written selectors.
     *                       The first argument is the selector key, the second is the value
     */
    void writeSelectors(@NotNull BiConsumer<String, String> appendSelector);
}
