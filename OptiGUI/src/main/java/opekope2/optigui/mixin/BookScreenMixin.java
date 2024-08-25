package opekope2.optigui.mixin;

import net.minecraft.client.gui.screen.ingame.BookScreen;
import opekope2.optigui.internal.interaction.InteractionHandler;
import opekope2.optigui.screen.IRetexturableScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BookScreen.class)
public abstract class BookScreenMixin implements IBookScreenAccessor, IRetexturableScreen {
    @Inject(method = "setPage", at = @At("RETURN"))
    private void setPageMixin(int index, CallbackInfoReturnable<Boolean> cir) {
        InteractionHandler.updateBookProperties(getPageIndex() + 1, callGetPageCount());
    }

    @Inject(method = "goToNextPage", at = @At("RETURN"))
    private void goToNextPageMixin(CallbackInfo ci) {
        InteractionHandler.updateBookProperties(getPageIndex() + 1, callGetPageCount());
    }

    @Inject(method = "goToPreviousPage", at = @At("RETURN"))
    private void goToPreviousPageMixin(CallbackInfo ci) {
        InteractionHandler.updateBookProperties(getPageIndex() + 1, callGetPageCount());
    }
}
