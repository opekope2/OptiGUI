package opekope2.optigui.screen.handler;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import opekope2.optigui.screen.ITextureChangeableScreen;

/**
 * A {@link ScreenHandler}, which has an inventory (at least one slot), and calculates redstone comparator using
 * {@link ScreenHandler#calculateComparatorOutput(Inventory)}.
 * <p>
 * Implementing this utility class on your {@link ScreenHandler} will add inventory and redstone comparator output
 * NBTs in our {@link HandledScreen}'s
 * {@link ITextureChangeableScreen#optiGui_writeNbt(NbtCompound, RegistryWrapper.WrapperLookup)} method.
 */
public interface IInventoryScreenHandler {
    /**
     * Gets the inventory of the screen handler.
     */
    Inventory optiGui_getInventory();
}
