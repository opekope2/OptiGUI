package opekope2.optigui.mixin.screen;

import net.minecraft.client.gui.screen.ingame.LecternScreen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.LecternScreenHandler;
import net.minecraft.util.math.MathHelper;
import opekope2.optigui.util.Constants;
import opekope2.optigui.util.INbtConvertible;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LecternScreen.class)
public abstract class LecternScreenMixin extends BookScreenMixin {
    @Shadow
    public abstract LecternScreenHandler getScreenHandler();

    @Override
    public void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup) {
        super.optiGui_writeNbt(compound, lookup);
        ((INbtConvertible) getScreenHandler()).optiGui_writeNbt(compound, lookup);
        float f = callGetPageCount() > 1 ? getPageIndex() / (callGetPageCount() - 1.0f) : 1.0f;
        compound.putInt(Constants.COMPARATOR_OUTPUT_KEY, MathHelper.floor(f * 14.0f) + 1);
    }
}
