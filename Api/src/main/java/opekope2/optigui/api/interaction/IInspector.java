package opekope2.optigui.api.interaction;

import opekope2.optigui.annotation.RequiresImplementation;
import opekope2.optigui.api.IOptiGuiApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface defining an interaction inspector.
 * An interaction inspector can create an OptiGUI INI representation of the selectors matching the current interaction.
 */
public interface IInspector {
    /**
     * Inspects the current interaction.
     *
     * @return An OptiGUI INI representation of one possible selector combination, which matches the current interaction,
     * or {@code null}, if there is no interaction ongoing
     */
    @Nullable
    String inspectCurrentInteraction();

    /**
     * Returns the implementation of {@link IInspector}.
     */
    @NotNull
    @RequiresImplementation
    static IInspector getInstance() {
        return IOptiGuiApi.getImplementation().getInteractionInspector();
    }
}
