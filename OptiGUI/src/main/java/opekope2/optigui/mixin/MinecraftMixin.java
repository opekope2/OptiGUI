package opekope2.optigui.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import opekope2.optigui.interaction.GeneralInteraction;
import opekope2.optigui.interaction.InteractionManager;
import opekope2.optigui.interaction.InteractionTarget;
import opekope2.optigui.internal.ui.ResourceLoadingLogScreen;
import opekope2.optigui.screen_api.screen.ITextureChangeableScreen;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Mixin(value = Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    public @Nullable LocalPlayer player;

    @Shadow
    public @Nullable Screen screen;

    @Inject(method = "setScreen(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("TAIL"))
    private void manageInteraction(CallbackInfo ci) {
        if (player == null) return;

        if (screen instanceof EffectRenderingInventoryScreen<?>) {
            InteractionManager.prepare(GeneralInteraction.factory(InteractionTarget.Inventory.INSTANCE, player, InteractionHand.MAIN_HAND));
        }

        if (screen instanceof ITextureChangeableScreen textureChangeableScreen) {
            InteractionManager.begin(textureChangeableScreen, player);
        } else if (screen == null) {
            InteractionManager.end();
        }
    }

    @Inject(method = "addInitialScreens", at = @At("TAIL"))
    private void showResourceLoadingErrors(List<Function<Runnable, Screen>> list, CallbackInfo ci) {
        if (ResourceLoadingLogScreen.shouldShow()) list.add(ResourceLoadingLogScreen::new);
    }

    @Inject(method = "reloadResourcePacks()Ljava/util/concurrent/CompletableFuture;", at = @At("RETURN"))
    private void showResourceLoadingErrors(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        cir.getReturnValue().thenRun(ResourceLoadingLogScreen::showIfErrorsOccurred);
    }
}
