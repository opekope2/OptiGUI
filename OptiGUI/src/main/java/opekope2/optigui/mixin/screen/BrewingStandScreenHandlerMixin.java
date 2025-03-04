package opekope2.optigui.mixin.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.ScreenHandler;
import opekope2.optigui.util.Constants;
import opekope2.optigui.util.INbtConvertible;
import opekope2.optigui.util.InventoryUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BrewingStandScreenHandler.class)
public abstract class BrewingStandScreenHandlerMixin implements INbtConvertible {
    @Shadow
    @Final
    private Inventory inventory;

    @Shadow
    public abstract int getFuel();

    @Shadow
    public abstract int getBrewTime();

    @Override
    public void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup) {
        compound.putInt(Constants.COMPARATOR_OUTPUT_KEY, ScreenHandler.calculateComparatorOutput(inventory));
        compound.put(Constants.INVENTORY_KEY, InventoryUtil.createNbt(inventory, lookup));
        compound.putInt("brew_time", getBrewTime());
        compound.putInt("fuel", getFuel());
    }
}
