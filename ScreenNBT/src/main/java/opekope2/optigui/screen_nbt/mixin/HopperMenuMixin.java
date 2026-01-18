package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_api.util.NbtUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HopperMenu.class)
public abstract class HopperMenuMixin implements INbtConvertible {
    @Shadow
    @Final
    private Container hopper;

    @Override
    public CompoundTag optiGui_asNbt(RegistryAccess registryAccess) {
        var compound = INbtConvertible.super.optiGui_asNbt(registryAccess);
        compound.putInt(COMPARATOR_OUTPUT_KEY, AbstractContainerMenu.getRedstoneSignalFromContainer(hopper));
        compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(hopper, registryAccess));
        return compound;
    }
}
