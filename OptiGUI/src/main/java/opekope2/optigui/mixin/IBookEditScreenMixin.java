package opekope2.optigui.mixin;

import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = BookEditScreen.class)
public interface IBookEditScreenMixin {
    @Accessor
    int getCurrentPage();

    @Invoker
    int invokeCountPages();
}
