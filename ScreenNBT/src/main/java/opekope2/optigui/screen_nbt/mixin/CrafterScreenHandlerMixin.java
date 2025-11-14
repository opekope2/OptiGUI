package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.CrafterScreenHandler;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_nbt.util.NbtUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CrafterScreenHandler.class)
public abstract class CrafterScreenHandlerMixin implements INbtConvertible {
    @Shadow
    public abstract Inventory getInputInventory();

    @Shadow
    @Final
    private CraftingResultInventory resultInventory;

    @Shadow
    public abstract boolean isSlotDisabled(int slot);

    @Shadow
    public abstract boolean isTriggered();

    @Override
    public void optiGui_writeNbt(NbtCompound compound, RegistryWrapper.WrapperLookup lookup) {
        compound.putInt(COMPARATOR_OUTPUT_KEY, calculateComparatorOutput(getInputInventory()));
        compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(getInputInventory(), lookup));
        compound.put(RESULT_INVENTORY_KEY, NbtUtil.createInventoryNbt(resultInventory, lookup));
        var enabledSlots = new NbtList();
        for (int i = 0; i < 9; i++) enabledSlots.add(NbtByte.of(!isSlotDisabled(i)));
        compound.put("enabled_slots", enabledSlots);
        compound.putBoolean("is_triggered", isTriggered());
    }

    @Unique
    private int calculateComparatorOutput(Inventory inventory) {
        var output = 0;
        for (int i = 0; i < inventory.size(); i++) {
            if (!inventory.getStack(i).isEmpty() || isSlotDisabled(i)) output++;
        }
        return output;
    }
}
