package opekope2.optigui.mixin.screen;

import net.minecraft.client.gui.screen.ingame.LecternScreen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import opekope2.optigui.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LecternScreen.class)
public abstract class LecternScreenMixin extends BookScreenMixin {
    protected LecternScreenMixin(Text title) {
        super(title);
    }

    @Override
    public void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup) {
        super.optiGui_writeNbt(compound, lookup);

        float f = callGetPageCount() > 1 ? getPageIndex() / (callGetPageCount() - 1.0f) : 1.0f;
        compound.putInt(Constants.COMPARATOR_OUTPUT_KEY, MathHelper.floor(f * 14.0f) + 1);
    }
}
