package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_api.util.NbtUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BrewingStandMenu.class)
public abstract class BrewingStandMenuMixin implements INbtConvertible {
    @Shadow
    @Final
    private Container brewingStand;

    @Shadow
    public abstract int getFuel();

    @Shadow
    public abstract int getBrewingTicks();

    @Override
    public CompoundTag optiGui_asNbt(RegistryAccess registryAccess) {
        var compound = INbtConvertible.super.optiGui_asNbt(registryAccess);
        compound.putInt(COMPARATOR_OUTPUT_KEY, AbstractContainerMenu.getRedstoneSignalFromContainer(brewingStand));
        compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(brewingStand, registryAccess));
        compound.putInt("brewing_ticks", getBrewingTicks());
        compound.putInt("fuel", getFuel());
        return compound;
    }
}
