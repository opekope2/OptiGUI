package opekope2.optigui.mixin;

import net.minecraft.client.gui.screen.ingame.BookScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = BookScreen.class)
public interface IBookScreenMixin {
    // Don't forget to add 1, because it's 0-indexed
    @Accessor
    int getPageIndex();

    @Invoker
    int invokeGetPageCount();
}
