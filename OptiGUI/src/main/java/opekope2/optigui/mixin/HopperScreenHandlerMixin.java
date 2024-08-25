package opekope2.optigui.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import opekope2.optigui.screen.IRedstoneComparatorOutputGetterScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HopperScreenHandler.class)
public abstract class HopperScreenHandlerMixin implements IRedstoneComparatorOutputGetterScreen {
    @Shadow
    @Final
    private Inventory inventory;

    @Override
    public int optiGUI_getRedstoneComparatorOutput() {
        return ScreenHandler.calculateComparatorOutput(inventory);
    }
}
