package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.AnvilScreenHandler;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandlerMixin {
    @Shadow
    public abstract int getLevelCost();

    @Shadow
    private @Nullable String newItemName;

    @Override
    public void optiGui_writeNbt(NbtCompound compound, RegistryWrapper.WrapperLookup lookup) {
        super.optiGui_writeNbt(compound, lookup);
        if (newItemName != null) compound.putString("new_name", newItemName);
        compound.putInt("cost", getLevelCost());
    }
}
