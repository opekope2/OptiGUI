package opekope2.optigui.mixin;

import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import opekope2.optigui.internal.interaction.InteractionHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BookEditScreen.class)
public abstract class BookEditScreenMixin {
    @Shadow
    public int currentPage;

    @Shadow
    public abstract int getNumPages();

    @Inject(method = "updatePageContent", at = @At("RETURN"))
    private void setPageMixin(CallbackInfo ci) {
        InteractionHandler.tryUpdateBookProperties(currentPage + 1, getNumPages());
    }
}
