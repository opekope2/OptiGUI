package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_api.util.NbtUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractFurnaceMenu.class)
public abstract class AbstractFurnaceMenuMixin implements INbtConvertible {
    @Shadow
    @Final
    private Container container;

    @Shadow
    public abstract float getBurnProgress();

    @Shadow
    public abstract float getLitProgress();

    @Shadow
    public abstract boolean isLit();

    @Override
    public CompoundTag optiGui_asNbt(RegistryAccess registryAccess) {
        var compound = INbtConvertible.super.optiGui_asNbt(registryAccess);
        compound.putInt(COMPARATOR_OUTPUT_KEY, AbstractContainerMenu.getRedstoneSignalFromContainer(container));
        compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(container, registryAccess));
        compound.putFloat("burn_progress", getBurnProgress());
        compound.putFloat("lit_progress", getLitProgress());
        compound.putBoolean("lit", isLit());
        return compound;
    }
}
