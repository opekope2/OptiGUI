package opekope2.optigui.api;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import opekope2.optigui.annotation.RequiresImplementation;
import opekope2.optigui.api.interaction.IBlockEntityProcessor;
import opekope2.optigui.api.interaction.IEntityProcessor;
import opekope2.optigui.api.interaction.IInteractor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The core OptiGUI API.
 */
public interface IOptiGuiApi {
    /**
     * Tells whether the OptiGUI implementation is loaded or not.
     * This should be checked before using any other OptiGUI API if OptiGUI is an optional dependency.
     * Such APIs are marked with {@link RequiresImplementation}.
     */
    boolean isAvailable();

    /**
     * Returns the ID of the mod, which provides OptiGUI implementation.
     */
    @NotNull
    String getImplementationModId();

    /**
     * Returns the implementation of {@see IInteractor}.
     *
     * @see IInteractor#getInstance()
     */
    @NotNull
    @RequiresImplementation
    IInteractor getInteractor();

    /**
     * Returns the container's inventory screen texture.
     *
     * @param container The container to find the screen texture of
     */
    @Nullable
    @RequiresImplementation
    Identifier getContainerTexture(@NotNull Identifier container);

    /**
     * Finds the registered entity processor for the given entity class.
     *
     * @param <T>  The type of the entity
     * @param type The class of the entity
     * @see IEntityProcessor#ofClass(Class)
     */
    @Nullable
    @RequiresImplementation
    <T extends Entity> IEntityProcessor<@NotNull T> getEntityProcessor(@NotNull Class<@NotNull T> type);

    /**
     * Finds the registered block entity processor for the given block entity class.
     *
     * @param <T>  The type of the block entity
     * @param type The class of the block entity
     * @see IBlockEntityProcessor#ofClass(Class)
     */
    @Nullable
    @RequiresImplementation
    <T extends BlockEntity> IBlockEntityProcessor<@NotNull T> getBlockEntityProcessor(@NotNull Class<@NotNull T> type);

    /**
     * Returns the implementation of {@link IOptiGuiApi}.
     */
    @NotNull
    static IOptiGuiApi getImplementation() {
        return IOptiGuiApi$Instance.get();
    }
}

/**
 * @hidden
 */
final class IOptiGuiApi$Instance implements IOptiGuiApi {
    @NotNull
    private static final IOptiGuiApi API = new IOptiGuiApi$Instance();

    private IOptiGuiApi$Instance() {
    }

    @NotNull
    public static IOptiGuiApi get() {
        return API;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    @NotNull
    public IInteractor getInteractor() {
        throw new UnsupportedOperationException("OptiGUI implementation is not available.");
    }

    @Override
    @Nullable
    public Identifier getContainerTexture(@NotNull Identifier container) {
        throw new UnsupportedOperationException("OptiGUI implementation is not available.");
    }

    @Override
    @Nullable
    public <T extends Entity> IEntityProcessor<@NotNull T> getEntityProcessor(@NotNull Class<@NotNull T> type) {
        throw new UnsupportedOperationException("OptiGUI implementation is not available.");
    }

    @Override
    @Nullable
    public <T extends BlockEntity> IBlockEntityProcessor<@NotNull T> getBlockEntityProcessor(@NotNull Class<@NotNull T> type) {
        throw new UnsupportedOperationException("OptiGUI implementation is not available.");
    }

    @Override
    @NotNull
    public String getImplementationModId() {
        return "optigui-api";
    }
}
