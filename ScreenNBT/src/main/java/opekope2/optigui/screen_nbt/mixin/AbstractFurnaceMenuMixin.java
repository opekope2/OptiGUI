package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.core.HolderLookup;
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
    public void optiGui_writeNbt(CompoundTag compound, HolderLookup.Provider lookup) {
        compound.putInt(COMPARATOR_OUTPUT_KEY, AbstractContainerMenu.getRedstoneSignalFromContainer(container));
        compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(container, lookup));
        compound.putFloat("burn_progress", getBurnProgress());
        compound.putFloat("lit_progress", getLitProgress());
        compound.putBoolean("lit", isLit());
    }
}
