package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.HorseInventoryMenu;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_api.util.NbtUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HorseInventoryMenu.class)
public abstract class HorseInventoryMenuMixin implements INbtConvertible {
    @Shadow
    @Final
    private Container horseContainer;

    @Shadow
    @Final
    private Container armorContainer;

    @Override
    public CompoundTag optiGui_asNbt(RegistryAccess registryAccess) {
        var compound = INbtConvertible.super.optiGui_asNbt(registryAccess);
        compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(horseContainer, registryAccess));
        compound.put("armor", NbtUtil.createInventoryNbt(armorContainer, registryAccess));
        return compound;
    }
}
