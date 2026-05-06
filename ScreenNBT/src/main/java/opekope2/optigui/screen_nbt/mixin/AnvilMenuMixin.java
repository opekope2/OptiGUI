package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.AnvilMenu;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenuMixin {
    @Shadow
    public abstract int getCost();

    @Shadow
    private @Nullable String itemName;

    @Override
    public CompoundTag optiGui_asNbt(RegistryAccess registryAccess) {
        var compound = super.optiGui_asNbt(registryAccess);
        if (itemName != null) compound.putString("item_name", itemName);
        compound.putInt("cost", getCost());
        return compound;
    }
}
