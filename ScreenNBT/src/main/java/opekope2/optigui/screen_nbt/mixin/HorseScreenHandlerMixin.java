package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.HorseInventoryMenu;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_nbt.util.NbtUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HorseInventoryMenu.class)
public abstract class HorseScreenHandlerMixin implements INbtConvertible {
    @Shadow
    @Final
    private Container horseContainer;

    @Shadow
    @Final
    private Container armorContainer;

    @Override
    public void optiGui_writeNbt(CompoundTag compound, HolderLookup.Provider lookup) {
        compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(horseContainer, lookup));
        compound.put("armor", NbtUtil.createInventoryNbt(armorContainer, lookup));
    }
}
