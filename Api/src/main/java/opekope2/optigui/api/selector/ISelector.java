package opekope2.optigui.api.selector;

import opekope2.lilac.annotation.EntrypointName;
import opekope2.optigui.api.interaction.Interaction;
import opekope2.optigui.filter.Filter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface defining an OptiGUI INI selector.
 * A selector creates a filter from its string description specified inside an INI file.
 * <br>
 * To register a selector, see {@link opekope2.optigui.annotation.Selector} documentation.
 * The name of the entrypoint is {@code optigui-selector}.
 *
 * @see opekope2.optigui.annotation.Selector
 */
@EntrypointName("optigui-selector")
public interface ISelector {
    /**
     * Creates a filter from a string description
     *
     * @param selector The value in an INI file for the key specified by the {@link opekope2.optigui.annotation.Selector} annotation
     * @return The created filter or {@code null}, if no such filter can be created
     * @apiNote Any exception thrown will be caught by OptiGUI, and its message will be logged
     */
    @Nullable
    Filter<Interaction, ?> createFilter(@NotNull String selector);
}
