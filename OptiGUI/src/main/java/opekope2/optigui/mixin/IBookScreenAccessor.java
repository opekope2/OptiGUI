package opekope2.optigui.mixin;

import net.minecraft.client.gui.screen.ingame.BookScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BookScreen.class)
public interface IBookScreenAccessor {
    @Accessor
    int getPageIndex();

    @Invoker
    int callGetPageCount();
}
