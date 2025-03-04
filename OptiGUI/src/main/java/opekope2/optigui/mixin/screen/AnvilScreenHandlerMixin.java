package opekope2.optigui.mixin.screen;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.AnvilScreenHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandlerMixin {
    @Shadow
    public abstract int getLevelCost();

    @Shadow
    @Nullable
    private String newItemName;

    @Override
    public void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup) {
        super.optiGui_writeNbt(compound, lookup);
        if (newItemName != null) compound.putString("new_name", newItemName);
        compound.putInt("cost", getLevelCost());
    }
}
