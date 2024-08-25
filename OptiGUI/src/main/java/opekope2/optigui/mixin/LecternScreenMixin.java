package opekope2.optigui.mixin;

import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.LecternScreen;
import net.minecraft.util.math.MathHelper;
import opekope2.optigui.screen.IRedstoneComparatorOutputGetterScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LecternScreen.class)
public abstract class LecternScreenMixin extends BookScreen implements IRedstoneComparatorOutputGetterScreen, IBookScreenAccessor {
    @Override
    public int optiGUI_getRedstoneComparatorOutput() {
        float f = getPageCount() > 1 ? getPageIndex() / (getPageCount() - 1.0f) : 1.0f;
        return MathHelper.floor(f * 14.0f) + 1;
    }
}
