package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.StonecutterScreenHandler;
import opekope2.optigui.screen_nbt.util.NbtUtil;
import opekope2.optigui.util.INbtConvertible;
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
    public void optiGui_writeNbt(NbtCompound compound, RegistryWrapper.WrapperLookup lookup) {
        compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(input, lookup));
        compound.put(RESULT_INVENTORY_KEY, NbtUtil.createInventoryNbt(output, lookup));
    }
}
