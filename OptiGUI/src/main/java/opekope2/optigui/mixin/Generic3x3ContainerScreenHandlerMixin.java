package opekope2.optigui.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import opekope2.optigui.screen.IRedstoneComparatorOutputGetterScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Generic3x3ContainerScreenHandler.class)
public abstract class Generic3x3ContainerScreenHandlerMixin implements IRedstoneComparatorOutputGetterScreen {
    @Shadow
    @Final
    private Inventory inventory;

    @Override
    public int optiGUI_getRedstoneComparatorOutput() {
        return ScreenHandler.calculateComparatorOutput(inventory);
    }
}
