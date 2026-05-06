package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.ResultContainer;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_api.util.NbtUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CrafterMenu.class)
public abstract class CrafterMenuMixin implements INbtConvertible {
    @Shadow
    public abstract Container getContainer();

    @Shadow
    @Final
    private ResultContainer resultContainer;

    @Shadow
    public abstract boolean isSlotDisabled(int slot);

    @Shadow
    public abstract boolean isPowered();

    @Override
    public CompoundTag optiGui_asNbt(RegistryAccess registryAccess) {
        var compound = INbtConvertible.super.optiGui_asNbt(registryAccess);
        compound.putInt(COMPARATOR_OUTPUT_KEY, optiGui_calculateComparatorOutput(getContainer()));
        compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(getContainer(), registryAccess));
        compound.put(RESULT_INVENTORY_KEY, NbtUtil.createInventoryNbt(resultContainer, registryAccess));
        compound.put("enabled_slots", optiGui_getEnabledSlots());
        compound.putBoolean("powered", isPowered());
        return compound;
    }

    @Unique
    private int optiGui_calculateComparatorOutput(Container inventory) {
        var output = 0;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (!inventory.getItem(i).isEmpty() || isSlotDisabled(i)) output++;
        }
        return output;
    }

    @Unique
    private ListTag optiGui_getEnabledSlots() {
        var enabledSlots = new ListTag();
        for (int i = 0; i < 9; i++) enabledSlots.add(ByteTag.valueOf(!isSlotDisabled(i)));
        return enabledSlots;
    }
}
