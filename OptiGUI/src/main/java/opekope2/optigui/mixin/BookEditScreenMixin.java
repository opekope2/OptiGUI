package opekope2.optigui.mixin;

import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import opekope2.optigui.internal.TextureReplacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BookEditScreen.class)
public abstract class BookEditScreenMixin {
    @Inject(method = "changePage", at = @At("RETURN"))
    private void setPageMixin(CallbackInfo ci) {
        TextureReplacer.forceTick();
    }
}
