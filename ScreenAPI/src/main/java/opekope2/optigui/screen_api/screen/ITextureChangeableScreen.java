package opekope2.optigui.screen_api.screen;

import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import opekope2.optigui.screen_api.util.INbtConvertible;

/**
 * Interface for allowing OptiGUI to change textures on a screen (and its subclasses).
 * <p>
 * If {@link INbtConvertible} is implemented into your {@link ITextureChangeableScreen}, OptiGUI will automatically call
 * {@link INbtConvertible#optiGui_writeNbt(CompoundTag, HolderLookup.Provider)} on it.
 * <p>
 * If OptiGUI Screen NBT mod is loaded (bundled by OptiGUI by default), and {@link INbtConvertible} is implemented into
 * your {@link AbstractContainerMenu}, the mod will automatically call
 * {@link INbtConvertible#optiGui_writeNbt(CompoundTag, HolderLookup.Provider)} on it.
 * <p>
 * Example implementation of {@link INbtConvertible#optiGui_writeNbt(CompoundTag, HolderLookup.Provider)}
 * <p>
 * <pre>
 * {@code
 * compound.put(SCREEN_TITLE_KEY, TextCodecs.CODEC.encodeStart(lookup.getOps(NbtOps.INSTANCE), getTitle()).getOrThrow());
 * compound.putInt(COMPARATOR_OUTPUT_KEY, ScreenHandler.calculateComparatorOutput(inventory));
 * compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(inventory, lookup));
 * }
 * </pre>
 * <p>
 * Mixed into {@link AbstractContainerScreen}, {@link BookViewScreen}, {@link BookEditScreen}, and {@link HangingSignEditScreen}.
 *
 * @see AbstractContainerMenu#getRedstoneSignalFromContainer(Container)
 */
public interface ITextureChangeableScreen {
    /**
     * {@link LayoutElement#setPosition(int, int) Sets the position} of the OptiGUI Inspector button.
     *
     * @param inspectorButton The OptiGUI Inspector button
     * @implNote It sits right on top of a (vanilla) screen, and is aligned to the right of it.
     */
    void optiGui_positionInspectorWidget(LayoutElement inspectorButton);
}
