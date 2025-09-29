package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.GrindstoneScreenHandler;
import opekope2.optigui.screen_nbt.util.NbtUtil;
import opekope2.optigui.util.INbtConvertible;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GrindstoneScreenHandler.class)
public abstract class GrindstoneScreenHandlerMixin implements INbtConvertible {
    @Shadow
    @Final
    Inventory input;

    @Shadow
    @Final
    private Inventory result;

    @Override
    public void optiGui_writeNbt(NbtCompound compound, RegistryWrapper.WrapperLookup lookup) {
        compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(input, lookup));
        compound.put(RESULT_INVENTORY_KEY, NbtUtil.createInventoryNbt(result, lookup));
    }
}
