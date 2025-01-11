package opekope2.optigui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.dynamic.Codecs;
import opekope2.optigui.util.Constants;
import opekope2.optigui.util.NbtUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Marker interface for allowing OptiGUI to change textures on a screen (and its subclasses).
 * <p>
 * Mixed into {@link HandledScreen}, {@link BookScreen}, and {@link BookEditScreen}.
 */
public interface ITextureChangeableScreen {
    /**
     * Writes the screen's content to {@code compound}.
     * By default, it writes {@link Screen#getTitle()} to key {@link Constants#SCREEN_TITLE_KEY}.
     *
     * @param compound The output {@link NbtCompound} to write contents to
     * @param lookup   The registry lookup used to encode NBT
     */
    default void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup) {
        if (this instanceof Screen screen) {
            NbtUtil.encode(compound, Constants.SCREEN_TITLE_KEY, screen.getTitle(), Codecs.TEXT, lookup);
        }
    }
}
