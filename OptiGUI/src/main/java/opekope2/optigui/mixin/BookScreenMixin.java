package opekope2.optigui.mixin;

import net.minecraft.client.gui.screen.ingame.BookScreen;
import opekope2.optigui.interaction.InteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BookScreen.class)
public abstract class BookScreenMixin {
    @Inject(method = "setPage", at = @At("RETURN"))
    private void clearInteractionCacheAfterPageChange(CallbackInfoReturnable<Boolean> cir) {
        InteractionManager.clearCache();
    }

    @Inject(method = {"goToNextPage", "goToPreviousPage"}, at = @At("RETURN"))
    private void clearInteractionCacheAfterPageChange(CallbackInfo ci) {
        InteractionManager.clearCache();
    }
}
