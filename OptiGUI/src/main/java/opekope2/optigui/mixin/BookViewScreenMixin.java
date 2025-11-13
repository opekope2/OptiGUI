package opekope2.optigui.mixin;

import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import opekope2.optigui.interaction.InteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BookViewScreen.class)
public abstract class BookViewScreenMixin {
    @Inject(method = "setPage", at = @At("RETURN"))
    private void clearInteractionCacheAfterPageChange(CallbackInfoReturnable<Boolean> cir) {
        InteractionManager.clearCache();
    }

    @Inject(method = {"pageForward", "pageBack"}, at = @At("RETURN"))
    private void clearInteractionCacheAfterPageChange(CallbackInfo ci) {
        InteractionManager.clearCache();
    }
}
