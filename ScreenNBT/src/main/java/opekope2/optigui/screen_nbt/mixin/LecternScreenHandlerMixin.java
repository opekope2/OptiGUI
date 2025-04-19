package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.LecternScreenHandler;
import opekope2.optigui.screen_nbt.util.NbtUtil;
import opekope2.optigui.util.INbtConvertible;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LecternScreenHandler.class)
public abstract class LecternScreenHandlerMixin implements INbtConvertible {
    @Shadow
    @Final
    private Inventory inventory;

    @Override
    public void optiGui_writeNbt(NbtCompound compound, RegistryWrapper.WrapperLookup lookup) {
        compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(inventory, lookup));
    }
}
