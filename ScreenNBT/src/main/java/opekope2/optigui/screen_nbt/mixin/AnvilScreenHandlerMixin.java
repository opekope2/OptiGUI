package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.AnvilMenu;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AnvilMenu.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandlerMixin {
    @Shadow
    public abstract int getCost();

    @Shadow
    private @Nullable String itemName;

    @Override
    public void optiGui_writeNbt(CompoundTag compound, HolderLookup.Provider lookup) {
        super.optiGui_writeNbt(compound, lookup);
        if (itemName != null) compound.putString("item_name", itemName);
        compound.putInt("cost", getCost());
    }
}
