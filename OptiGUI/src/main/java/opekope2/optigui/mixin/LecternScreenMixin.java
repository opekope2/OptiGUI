package opekope2.optigui.mixin;

import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.LecternScreen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.MathHelper;
import opekope2.optigui.screen.IRetexturableScreen;
import opekope2.optigui.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LecternScreen.class)
public abstract class LecternScreenMixin extends BookScreen implements IRetexturableScreen, IBookScreenAccessor {
    @Override
    public void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup) {
        float f = callGetPageCount() > 1 ? getPageIndex() / (callGetPageCount() - 1.0f) : 1.0f;
        compound.putInt(Constants.COMPARATOR_OUTPUT_KEY, MathHelper.floor(f * 14.0f) + 1);
        compound.putInt(Constants.CURRENT_PAGE_KEY, getPageIndex() + 1);
        compound.putInt(Constants.PAGE_COUNT_KEY, callGetPageCount());
    }
}
