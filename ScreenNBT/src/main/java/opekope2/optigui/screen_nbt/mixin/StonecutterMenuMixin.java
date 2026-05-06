package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.StonecutterMenu;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_api.util.NbtUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StonecutterMenu.class)
public abstract class StonecutterMenuMixin implements INbtConvertible {
    @Shadow
    @Final
    public Container container;

    @Shadow
    @Final
    ResultContainer resultContainer;

    @Override
    public CompoundTag optiGui_asNbt(RegistryAccess registryAccess) {
        var compound = INbtConvertible.super.optiGui_asNbt(registryAccess);
        compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(container, registryAccess));
        compound.put(RESULT_INVENTORY_KEY, NbtUtil.createInventoryNbt(resultContainer, registryAccess));
        return compound;
    }
}
