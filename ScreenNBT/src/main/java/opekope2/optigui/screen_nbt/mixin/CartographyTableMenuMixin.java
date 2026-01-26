package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ResultContainer;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_api.util.NbtUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CartographyTableMenu.class)
public abstract class CartographyTableMenuMixin implements INbtConvertible {
    @Shadow
    @Final
    public Container container;

    @Shadow
    @Final
    private ResultContainer resultContainer;

    @Override
    public CompoundTag optiGui_asNbt(RegistryAccess registryAccess) {
        var compound = INbtConvertible.super.optiGui_asNbt(registryAccess);
        compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(container, registryAccess));
        compound.put(RESULT_INVENTORY_KEY, NbtUtil.createInventoryNbt(resultContainer, registryAccess));
        return compound;
    }
}
