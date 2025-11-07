package opekope2.optigui.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import opekope2.optigui.gui.screen.ResourceLoadingErrorScreen;
import opekope2.optigui.interaction.GeneralInteraction;
import opekope2.optigui.interaction.InteractionManager;
import opekope2.optigui.interaction.InteractionTarget;
import opekope2.optigui.screen_api.screen.ITextureChangeableScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Mixin(value = MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Nullable
    public Screen currentScreen;

    @Inject(method = "setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("TAIL"))
    private void manageInteraction(CallbackInfo ci) {
        if (player == null) return;

        if (currentScreen instanceof AbstractInventoryScreen<?>) {
            InteractionManager.prepare(GeneralInteraction.factory(InteractionTarget.Inventory.INSTANCE, player, Hand.MAIN_HAND));
        }

        if (currentScreen instanceof ITextureChangeableScreen textureChangeableScreen) {
            InteractionManager.begin(textureChangeableScreen, player);
        } else {
            InteractionManager.end();
        }
    }

    @Inject(method = "createInitScreens", at = @At("TAIL"))
    private void showResourceLoadingErrors(List<Function<Runnable, Screen>> list, CallbackInfo ci) {
        if (ResourceLoadingErrorScreen.shouldShow()) list.add(ResourceLoadingErrorScreen::create);
    }

    @Inject(method = "reloadResources()Ljava/util/concurrent/CompletableFuture;", at = @At("RETURN"))
    private void showResourceLoadingErrors(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        cir.getReturnValue().thenRun(ResourceLoadingErrorScreen::showIfErrorsOccurred);
    }
}
