package opekope2.optigui.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import opekope2.optigui.internal.TextureReplacer;
import opekope2.optigui.internal.interaction.InteractionHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Gui.class)
abstract class GuiMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "setScreen(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("TAIL"))
    private void setScreenMixin(Screen screen, CallbackInfo ci) {
        LocalPlayer player = minecraft.player;

        if (player != null && screen != null) {
            InteractionHandler.interact(player, player.level(), screen);
        }

        TextureReplacer.handleScreenChange(screen);
    }
}
