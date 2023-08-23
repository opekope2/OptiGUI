package opekope2.optigui.api;

import opekope2.lilac.annotation.EntrypointName;
import org.jetbrains.annotations.NotNull;

/**
 * OptiGUI entrypoint.
 * Add an <a href="https://fabricmc.net/wiki/documentation:entrypoint">entrypoint</a> named {@code optigui}
 * implementing {@code IOptiGuiEntrypoint} to {@code fabric.mod.json}.
 */
@FunctionalInterface
@EntrypointName("optigui")
public interface IEntrypoint {
    /**
     * Called by OptiGUI during initialization. If this method gets called, it's a good indicator that OptiGUI is loaded,
     * and OptiGUI-related code can be initialized here.
     *
     * @param context The entrypoint context
     */
    void initialize(@NotNull IEntrypoint.IInitializationContext context);

    /**
     * Initialization context for OptiGUI-compatible mods.
     */
    interface IInitializationContext {
    }
}
