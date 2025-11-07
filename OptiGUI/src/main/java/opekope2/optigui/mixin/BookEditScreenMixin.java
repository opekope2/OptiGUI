package opekope2.optigui.mixin;

import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import opekope2.optigui.interaction.InteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BookEditScreen.class)
public abstract class BookEditScreenMixin {
    @Inject(method = "changePage", at = @At("RETURN"))
    private void clearInteractionCacheAfterPageChange(CallbackInfo ci) {
        InteractionManager.clearCache();
    }
}
