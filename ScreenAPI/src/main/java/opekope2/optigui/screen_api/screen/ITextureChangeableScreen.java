package opekope2.optigui.screen_api.screen;

import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import opekope2.optigui.screen_api.util.INbtConvertible;

/**
 * Marker interface for allowing OptiGUI to change textures on a screen (and its subclasses).
 * <p>
 * If {@link INbtConvertible} is implemented into your {@link ITextureChangeableScreen}, OptiGUI will automatically call
 * {@link INbtConvertible#optiGui_writeNbt(NbtCompound, RegistryWrapper.WrapperLookup)} on it.
 * <p>
 * If OptiGUI Screen NBT Extension mod is installed, and {@link INbtConvertible} is implemented into your
 * {@link ScreenHandler}, the mod will automatically call
 * {@link INbtConvertible#optiGui_writeNbt(NbtCompound, RegistryWrapper.WrapperLookup)} on it.
 * <p>
 * Example implementation of {@link INbtConvertible#optiGui_writeNbt(NbtCompound, RegistryWrapper.WrapperLookup)}
 * <p>
 * <pre>
 * {@code
 * compound.put(SCREEN_TITLE_KEY, TextCodecs.CODEC.encodeStart(lookup.getOps(NbtOps.INSTANCE), getTitle()).getOrThrow());
 * compound.putInt(COMPARATOR_OUTPUT_KEY, ScreenHandler.calculateComparatorOutput(inventory));
 * compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(inventory, lookup));
 * }
 * </pre>
 * <p>
 * Mixed into {@link HandledScreen}, {@link BookScreen}, {@link BookEditScreen}, and {@link HangingSignEditScreen}.
 *
 * @see ScreenHandler#calculateComparatorOutput(Inventory)
 */
public interface ITextureChangeableScreen {
    /**
     * {@link Widget#setPosition(int, int) Sets the position} of the OptiGUI Inspector button.
     *
     * @param inspectorButton The OptiGUI Inspector button
     * @implNote It sits right on top of a (vanilla) screen, and is aligned to the right of it.
     */
    void optiGui_positionInspectorWidget(Widget inspectorButton);
}
