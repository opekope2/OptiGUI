package opekope2.optigui.screen_api.screen;

import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_api.util.NbtUtil;

/**
 * Interface for allowing OptiGUI to change textures on a screen (and its subclasses).
 * <p>
 * The following NBT data is automatically merged into the screen NBT data:
 * <ul>
 * <li>{@link Screen#getTitle() The screen's title}</li>
 * <li>If your screen implements {@link RecipeUpdateListener}: whether the recipe book is {@link RecipeBookComponent#isVisible() visible}</li>
 * <li>If your screen implements {@link MenuAccess}, and {@link MenuAccess#getMenu() which} implements {@link INbtConvertible}: the return value of {@link INbtConvertible#optiGui_asNbt(RegistryAccess)} invoked on the menu</li>
 * </ul>
 * Example implementation of {@link INbtConvertible#optiGui_asNbt(RegistryAccess)}:
 * <p>
 * <pre>
 * {@code
 * compound.putInt(COMPARATOR_OUTPUT_KEY, AbstractContainerMenu.getRedstoneSignalFromContainer(container));
 * compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(container, lookup));
 * }
 * </pre>
 * <p>
 * Screen NBT data is only available if OptiGUI Screen NBT mod is loaded, which is bundled by an official/unmodified release of OptiGUI.
 * <p>
 * Mixed into {@link AbstractContainerScreen}, {@link CreativeModeInventoryScreen}, {@link BookEditScreen}, {@link BookViewScreen}, and {@link HangingSignEditScreen}.
 *
 * @see AbstractContainerMenu#getRedstoneSignalFromContainer(Container)
 * @see NbtUtil
 */
public interface ITextureChangeableScreen extends INbtConvertible {
    /**
     * {@link LayoutElement#setPosition(int, int) Sets the position} of the OptiGUI Inspector button.
     * It sits right on top of a (vanilla) screen, and is aligned to the right of it.
     *
     * @param inspectorButton The OptiGUI Inspector button
     */
    void optiGui_positionInspectorWidget(LayoutElement inspectorButton);

    /**
     * Casts this object to a {@link Screen}.
     */
    default Screen optiGui_asScreen() {
        return (Screen) this;
    }
}
