package opekope2.optigui.screen;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.NotNull;

/**
 * Marker interface for making a screen retexturable by OptiGUI.
 */
public interface IRetexturableScreen {
    /**
     * Writes the screen's content to {@code compound}.
     *
     * @param compound The output {@link NbtCompound} to write contents to
     * @param lookup   The registry lookup used to encode NBT
     */
    default void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup) {
    }
}
