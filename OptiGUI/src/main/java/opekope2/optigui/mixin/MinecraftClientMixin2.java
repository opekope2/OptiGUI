package opekope2.optigui.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.world.World;
import opekope2.optigui.internal.interaction.InteractionHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Inject before MinecraftClientMixin begins interaction, but leave room for other mods
@Mixin(value = MinecraftClient.class,  priority = 900)
abstract class MinecraftClientMixin2 {
    @Inject(method = "setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("TAIL"))
    private void setScreenMixin(Screen screen, CallbackInfo ci) {
        @SuppressWarnings("DataFlowIssue")
        MinecraftClient thiz = (MinecraftClient) (Object) this;
        ClientPlayerEntity player = thiz.player;
        if (player != null) {
            World world = player.getEntityWorld();
            Screen currentScreen = thiz.currentScreen;
            if (world != null && currentScreen != null) {
                InteractionHandler.interact(player, world, currentScreen);
            }
        }
    }
}
