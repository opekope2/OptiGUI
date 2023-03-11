package opekope2.optigui.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import opekope2.optigui.internal.TextureReplacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftClient.class)
abstract class MinecraftClientMixin {
    @Inject(method = "setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("TAIL"))
    private void setScreenMixin(Screen screen, CallbackInfo ci) {
        MinecraftClient thiz = (MinecraftClient) (Object) this;
        TextureReplacer.handleScreenChange(thiz.currentScreen);
    }
}
