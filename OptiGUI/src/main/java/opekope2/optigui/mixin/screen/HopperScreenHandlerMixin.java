package opekope2.optigui.mixin.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.HopperScreenHandler;
import opekope2.optigui.screen.handler.IInventoryScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HopperScreenHandler.class)
public abstract class HopperScreenHandlerMixin implements IInventoryScreenHandler {
    @Accessor
    @Override
    public abstract Inventory getInventory();
}
