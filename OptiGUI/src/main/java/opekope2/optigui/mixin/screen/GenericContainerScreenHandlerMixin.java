package opekope2.optigui.mixin.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import opekope2.optigui.screen.handler.IInventoryScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GenericContainerScreenHandler.class)
public abstract class GenericContainerScreenHandlerMixin implements IInventoryScreenHandler {
    @Accessor("inventory")
    @Override
    public abstract Inventory optiGui_getInventory();
}
