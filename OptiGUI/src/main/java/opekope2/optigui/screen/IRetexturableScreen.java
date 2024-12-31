package opekope2.optigui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.TextCodecs;
import opekope2.optigui.util.Constants;
import opekope2.optigui.util.NbtUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Marker interface for making a screen retexturable by OptiGUI.
 */
public interface IRetexturableScreen {
    /**
     * Writes the screen's content to {@code compound}.
     * By default, it writes {@link Screen#getTitle()} to key {@link Constants#SCREEN_TITLE_KEY}.
     *
     * @param compound The output {@link NbtCompound} to write contents to
     * @param lookup   The registry lookup used to encode NBT
     */
    default void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup) {
        if (this instanceof Screen screen) {
            NbtUtil.encode(compound, Constants.SCREEN_TITLE_KEY, screen.getTitle(), TextCodecs.CODEC, lookup);
        }
    }
}
