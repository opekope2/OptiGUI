package opekope2.optigui.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import opekope2.optigui.screen.IRedstoneComparatorOutputGetterScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GenericContainerScreenHandler.class)
public abstract class GenericContainerScreenHandlerMixin implements IRedstoneComparatorOutputGetterScreen {
    @Shadow
    public abstract Inventory getInventory();

    @Override
    public int optiGUI_getRedstoneComparatorOutput() {
        return ScreenHandler.calculateComparatorOutput(getInventory());
    }
}
