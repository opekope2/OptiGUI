package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ResultContainer;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_nbt.util.NbtUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CartographyTableMenu.class)
public abstract class CartographyTableScreenHandlerMixin implements INbtConvertible {
    @Shadow
    @Final
    public Container container;

    @Shadow
    @Final
    private ResultContainer resultContainer;

    @Override
    public void optiGui_writeNbt(CompoundTag compound, HolderLookup.Provider lookup) {
        compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(container, lookup));
        compound.put(RESULT_INVENTORY_KEY, NbtUtil.createInventoryNbt(resultContainer, lookup));
    }
}
