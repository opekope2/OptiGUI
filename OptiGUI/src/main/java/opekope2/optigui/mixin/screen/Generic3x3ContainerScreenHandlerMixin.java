package opekope2.optigui.mixin.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import opekope2.optigui.util.Constants;
import opekope2.optigui.util.INbtConvertible;
import opekope2.optigui.util.InventoryUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Generic3x3ContainerScreenHandler.class)
public abstract class Generic3x3ContainerScreenHandlerMixin implements INbtConvertible {
    @Shadow
    @Final
    private Inventory inventory;

    @Override
    public void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup) {
        compound.putInt(Constants.COMPARATOR_OUTPUT_KEY, ScreenHandler.calculateComparatorOutput(inventory));
        compound.put(Constants.INVENTORY_KEY, InventoryUtil.createNbt(inventory, lookup));
    }
}
