package opekope2.optigui.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.BrewingStandScreenHandler;
import opekope2.optigui.screen.handler.IInventoryScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BrewingStandScreenHandler.class)
public abstract class BrewingStandScreenHandlerMixin implements IInventoryScreenHandler {
    @Accessor
    @Override
    public abstract Inventory getInventory();
}
