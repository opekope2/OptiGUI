package opekope2.optigui.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import opekope2.optigui.internal.TextureReplacer;
import opekope2.optigui.internal.interaction.InteractionHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftClient.class)
abstract class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Nullable
    public Screen currentScreen;

    @Shadow public abstract void setScreen(@Nullable Screen screen);

    @Inject(method = "setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("TAIL"))
    private void setScreenMixin(Screen screen, CallbackInfo ci) {
        if (player != null && player.getEntityWorld() != null && currentScreen != null) {
            InteractionHandler.interact(player, player.getEntityWorld(), currentScreen);
        }

        TextureReplacer.handleScreenChange(currentScreen);
    }
}
