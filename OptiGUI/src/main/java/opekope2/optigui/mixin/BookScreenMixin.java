package opekope2.optigui.mixin;

import net.minecraft.client.gui.screen.ingame.BookScreen;
import opekope2.optigui.internal.TextureReplacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BookScreen.class)
public abstract class BookScreenMixin {
    @Inject(method = "setPage", at = @At("RETURN"))
    private void setPageMixin(int index, CallbackInfoReturnable<Boolean> cir) {
        TextureReplacer.refreshInteractionData();
    }

    @Inject(method = "goToNextPage", at = @At("RETURN"))
    private void goToNextPageMixin(CallbackInfo ci) {
        TextureReplacer.refreshInteractionData();
    }

    @Inject(method = "goToPreviousPage", at = @At("RETURN"))
    private void goToPreviousPageMixin(CallbackInfo ci) {
        TextureReplacer.refreshInteractionData();
    }
}
