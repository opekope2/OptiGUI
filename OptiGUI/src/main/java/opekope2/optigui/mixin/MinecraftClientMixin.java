package opekope2.optigui.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import opekope2.optigui.interaction.InteractionManager;
import opekope2.optigui.interaction.data.InteractionPlayerData;
import opekope2.optigui.interaction.data.InventoryInteractionData;
import opekope2.optigui.screen.IRetexturableScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Nullable
    public Screen currentScreen;

    @Inject(method = "setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("TAIL"))
    private void setScreenMixin(CallbackInfo ci) {
        if (player != null && currentScreen instanceof AbstractInventoryScreen<?>) {
            InteractionManager.prepare(
                    new InventoryInteractionData(
                            player.getMainHandStack(),
                            new InteractionPlayerData(player, Hand.MAIN_HAND)
                    )
            );
        }

        if (currentScreen instanceof IRetexturableScreen retexturableScreen) {
            InteractionManager.begin$optigui(retexturableScreen);
        } else {
            InteractionManager.end$optigui();
        }
    }
}
