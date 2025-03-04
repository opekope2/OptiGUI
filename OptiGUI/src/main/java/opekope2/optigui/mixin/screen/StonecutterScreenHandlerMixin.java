package opekope2.optigui.mixin.screen;

import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.StonecutterScreenHandler;
import opekope2.optigui.util.Constants;
import opekope2.optigui.util.INbtConvertible;
import opekope2.optigui.util.InventoryUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StonecutterScreenHandler.class)
public abstract class StonecutterScreenHandlerMixin implements INbtConvertible {
    @Shadow
    @Final
    public Inventory input;

    @Shadow
    @Final
    CraftingResultInventory output;

    @Override
    public void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup) {
        compound.put(Constants.INVENTORY_KEY, InventoryUtil.createNbt(input, lookup));
        compound.put(Constants.RESULT_INVENTORY_KEY, InventoryUtil.createNbt(output, lookup));
    }
}
