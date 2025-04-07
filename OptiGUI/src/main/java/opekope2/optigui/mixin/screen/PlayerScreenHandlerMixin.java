package opekope2.optigui.mixin.screen;

import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PlayerScreenHandler;
import opekope2.optigui.util.Constants;
import opekope2.optigui.util.INbtConvertible;
import opekope2.optigui.util.InventoryUtil;
import org.jetbrains.annotations.NotNull;
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
    public void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup) {
        compound.put("crafting_inventory", InventoryUtil.createNbt(getCraftingInput(), lookup));
        compound.put(Constants.RESULT_INVENTORY_KEY, InventoryUtil.createNbt(craftingResult, lookup));
    }
}
