package opekope2.optigui.mixin;

import net.minecraft.client.gui.screen.ingame.BookScreen;
import opekope2.lilac.api.ILilacApi;
import opekope2.lilac.api.tick.ITickNotifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BookScreen.class)
public abstract class BookScreenMixin {
    @Unique
    private static final ITickNotifier tickNotifier = ILilacApi.getImplementation().getTickNotifier();

    @Inject(method = "setPage", at = @At("RETURN"))
    private void setPageMixin(int index, CallbackInfoReturnable<Boolean> cir) {
        tickNotifier.forceTick();
    }

    @Inject(method = "goToNextPage", at = @At("RETURN"))
    private void goToNextPageMixin(CallbackInfo ci) {
        tickNotifier.forceTick();
    }

    @Inject(method = "goToPreviousPage", at = @At("RETURN"))
    private void goToPreviousPageMixin(CallbackInfo ci) {
        tickNotifier.forceTick();
    }
}
