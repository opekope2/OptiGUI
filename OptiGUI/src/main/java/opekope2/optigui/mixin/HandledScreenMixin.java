package opekope2.optigui.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import opekope2.optigui.screen.IRetexturableScreen;
import opekope2.optigui.screen.handler.IInventoryScreenHandler;
import opekope2.optigui.util.Constants;
import opekope2.optigui.util.InventoryUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin implements IRetexturableScreen {
    @Shadow
    public abstract ScreenHandler getScreenHandler();

    @Override
    public void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup) {
        IRetexturableScreen.super.optiGui_writeNbt(compound, lookup);

        ScreenHandler screenHandler = getScreenHandler();
        if (screenHandler instanceof IInventoryScreenHandler inventoryScreenHandler) {
            Inventory inventory = inventoryScreenHandler.getInventory();

            compound.putInt(Constants.COMPARATOR_OUTPUT_KEY, ScreenHandler.calculateComparatorOutput(inventory));
            compound.put(Constants.INVENTORY_KEY, InventoryUtil.createNbt(inventory, lookup));
        }
    }
}
