package opekope2.optigui.mixin;

import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import opekope2.optigui.internal.interaction.InteractionHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BookViewScreen.class)
public abstract class BookViewScreenMixin {
    @Shadow
    public int currentPage;

    @Shadow
    public abstract int getNumPages();

    @Inject(method = "setPage", at = @At("RETURN"))
    private void setPageMixin(int index, CallbackInfoReturnable<Boolean> cir) {
        InteractionHandler.tryUpdateBookProperties(currentPage + 1, getNumPages());
    }

    @Inject(method = "pageForward", at = @At("RETURN"))
    private void goToNextPageMixin(CallbackInfo ci) {
        InteractionHandler.tryUpdateBookProperties(currentPage + 1, getNumPages());
    }

    @Inject(method = "pageBack", at = @At("RETURN"))
    private void goToPreviousPageMixin(CallbackInfo ci) {
        InteractionHandler.tryUpdateBookProperties(currentPage + 1, getNumPages());
    }
}
