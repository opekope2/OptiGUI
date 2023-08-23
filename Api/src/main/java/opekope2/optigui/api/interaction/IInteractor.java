package opekope2.optigui.api.interaction;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import opekope2.optigui.annotation.RequiresImplementation;
import opekope2.optigui.api.IOptiGuiApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for signaling OptiGUI about interactions for texture replacing.
 */
public interface IInteractor {
    /**
     * Signals the texture replacer that an interaction has begun. Should be called before opening a GUI.
     *
     * @param player    The interacting player
     * @param world     The world the interaction happens in. Must be client-side
     * @param hand      The hand of the player that triggered the interaction
     * @param target    The target the player is interacting with
     * @param hitResult The hit result from Minecraft
     * @return {@code true} if the parameters are valid, and a GUI is not open, otherwise {@code false}
     */
    boolean interact(@NotNull PlayerEntity player, @NotNull World world, @NotNull Hand hand, @NotNull IInteractionTarget target, @Nullable HitResult hitResult);

    /**
     * Returns the implementation of {@link IInteractor}.
     */
    @NotNull
    @RequiresImplementation
    static IInteractor getInstance() {
        return IOptiGuiApi.getImplementation().getInteractor();
    }
}
