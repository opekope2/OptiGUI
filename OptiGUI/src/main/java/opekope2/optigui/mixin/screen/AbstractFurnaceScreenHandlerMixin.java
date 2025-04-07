package opekope2.optigui.mixin.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import opekope2.optigui.util.Constants;
import opekope2.optigui.util.INbtConvertible;
import opekope2.optigui.util.InventoryUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractFurnaceScreenHandler.class)
public abstract class AbstractFurnaceScreenHandlerMixin implements INbtConvertible {
    @Shadow
    @Final
    private Inventory inventory;

    @Shadow
    public abstract float getCookProgress();

    @Shadow
    public abstract float getFuelProgress();

    @Shadow
    public abstract boolean isBurning();

    @Override
    public void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup) {
        compound.putInt(Constants.COMPARATOR_OUTPUT_KEY, ScreenHandler.calculateComparatorOutput(inventory));
        compound.put(Constants.INVENTORY_KEY, InventoryUtil.createNbt(inventory, lookup));
        compound.putFloat("cook_progress", getCookProgress());
        compound.putFloat("fuel_progress", getFuelProgress());
        compound.putBoolean("is_burning", isBurning());
    }
}
