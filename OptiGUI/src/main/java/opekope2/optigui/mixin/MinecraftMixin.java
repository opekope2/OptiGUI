package opekope2.optigui.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import opekope2.optigui.internal.TextureReplacer;
import opekope2.optigui.internal.interaction.InteractionHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class)
abstract class MinecraftMixin {
    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    @Nullable
    public Screen screen;

    @Inject(method = "setScreen(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("TAIL"))
    private void setScreenMixin(Screen screen, CallbackInfo ci) {
        if (player != null && player.level() != null && screen != null) {
            InteractionHandler.interact(player, player.level(), screen);
        }

        TextureReplacer.handleScreenChange(screen);
    }
}
