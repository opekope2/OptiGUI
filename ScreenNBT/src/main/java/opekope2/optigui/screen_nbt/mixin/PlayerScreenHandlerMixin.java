package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PlayerScreenHandler;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_nbt.util.NbtUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin implements INbtConvertible {
    @Shadow
    @Final
    private CraftingResultInventory craftingResult;

    @Shadow
    public abstract RecipeInputInventory getCraftingInput();

    @Override
    public void optiGui_writeNbt(NbtCompound compound, RegistryWrapper.WrapperLookup lookup) {
        compound.put("crafting_inventory", NbtUtil.createInventoryNbt(getCraftingInput(), lookup));
        compound.put(RESULT_INVENTORY_KEY, NbtUtil.createInventoryNbt(craftingResult, lookup));
    }
}
