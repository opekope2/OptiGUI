package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultContainer;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_api.util.NbtUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin implements INbtConvertible {
    @Shadow
    @Final
    private ResultContainer resultSlots;

    @Shadow
    public abstract CraftingContainer getCraftSlots();

    @Override
    public CompoundTag optiGui_asNbt(RegistryAccess registryAccess) {
        var compound = INbtConvertible.super.optiGui_asNbt(registryAccess);
        compound.put("crafting_grid", NbtUtil.createInventoryNbt(getCraftSlots(), registryAccess));
        compound.put(RESULT_INVENTORY_KEY, NbtUtil.createInventoryNbt(resultSlots, registryAccess));
        return compound;
    }
}
