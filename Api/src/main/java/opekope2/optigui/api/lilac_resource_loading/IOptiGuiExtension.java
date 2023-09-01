package opekope2.optigui.api.lilac_resource_loading;

import net.minecraft.util.Identifier;
import opekope2.lilac.api.resource.loading.IResourceLoader;
import opekope2.lilac.api.resource.loading.IResourceLoadingSession;
import opekope2.optigui.api.interaction.Interaction;
import opekope2.optigui.filter.Filter;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * OptiGUI extension for {@link IResourceLoadingSession}.
 * Can be obtained with {@link IResourceLoadingSession#getExtension(String)}, where {@code modId} is {@code optigui}.
 * <br>
 * Recommended way of obtaining an instance is in {@link IResourceLoader.IFactory#createResourceLoader(IResourceLoadingSession)},
 * and the session extension instance should be passed to the created {@link IResourceLoader}.
 *
 * @apiNote Must be closed (for example, in {@link IResourceLoader#close()}), otherwise OptiGUI will not finish loading resources.
 */
public interface IOptiGuiExtension extends AutoCloseable {
    /**
     * Adds a filter to the OptiGUI filter chain.
     *
     * @param resource            The resource the filter was created from, useful for OptiGUI or resource pack debugging
     * @param filter              The filter to add
     * @param replaceableTextures The textures the filter can replace
     * @param priority            The priority of the filter. Filters with higher priorities will be processed first.
     */
    void addFilter(
            @NotNull Identifier resource,
            @NotNull Filter<Interaction, Identifier> filter,
            @NotNull Set<Identifier> replaceableTextures,
            int priority
    );

    /**
     * Adds a filter to the OptiGUI filter chain with default ({@code 0}) priority.
     *
     * @param resource            The resource the filter was created from, useful for OptiGUI or resource pack debugging
     * @param filter              The filter to add
     * @param replaceableTextures The textures the filter can replace
     * @see #addFilter(Identifier, Filter, Set, int)
     */
    default void addFilter(
            @NotNull Identifier resource,
            @NotNull Filter<Interaction, Identifier> filter,
            @NotNull Set<Identifier> replaceableTextures
    ) {
        addFilter(resource, filter, replaceableTextures, 0);
    }

    /**
     * Logs a warning caused by a resource (for example, it is malformed).
     *
     * @param resource The resource, which caused the warning
     * @param message  The warning/error message
     */
    void warn(@NotNull Identifier resource, @NotNull String message);
}
