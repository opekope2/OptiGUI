package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.CraftingScreenHandler;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_nbt.util.NbtUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CraftingScreenHandler.class)
public abstract class CraftingScreenHandlerMixin implements INbtConvertible {
    @Shadow
    @Final
    private RecipeInputInventory input;

    @Shadow
    @Final
    private CraftingResultInventory result;

    @Override
    public void optiGui_writeNbt(NbtCompound compound, RegistryWrapper.WrapperLookup lookup) {
        compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(input, lookup));
        compound.put(RESULT_INVENTORY_KEY, NbtUtil.createInventoryNbt(result, lookup));
    }
}
