package opekope2.optigui.mixin.screen;

import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.CraftingScreenHandler;
import opekope2.optigui.util.Constants;
import opekope2.optigui.util.INbtConvertible;
import opekope2.optigui.util.InventoryUtil;
import org.jetbrains.annotations.NotNull;
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
    public void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup) {
        compound.put(Constants.INVENTORY_KEY, InventoryUtil.createNbt(input, lookup));
        compound.put(Constants.RESULT_INVENTORY_KEY, InventoryUtil.createNbt(result, lookup));
    }
}
