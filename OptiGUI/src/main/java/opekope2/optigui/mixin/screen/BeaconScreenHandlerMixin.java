package opekope2.optigui.mixin.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.BeaconScreenHandler;
import opekope2.optigui.util.Constants;
import opekope2.optigui.util.INbtConvertible;
import opekope2.optigui.util.InventoryUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BeaconScreenHandler.class)
public abstract class BeaconScreenHandlerMixin implements INbtConvertible {
    @Shadow
    @Final
    private Inventory payment;

    @Override
    public void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup) {
        compound.put(Constants.INVENTORY_KEY, InventoryUtil.createNbt(payment, lookup));
    }
}
