package opekope2.optigui.mixin;

import net.minecraft.nbt.TextComponentTagVisitor;
import opekope2.optigui.internal.ui.DebuggerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextComponentTagVisitor.class)
public interface TextComponentTagVisitorAccessor extends DebuggerScreen.InputNbtVisitor.IAccessor {
    @Override
    @Accessor("depth")
    void optiGui_setDepth(int depth);

    @Accessor("MAX_DEPTH")
    static int optiGui_getMaxDepth() {
        throw new AssertionError();
    }
}
