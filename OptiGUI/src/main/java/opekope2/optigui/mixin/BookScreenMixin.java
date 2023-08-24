package opekope2.optigui.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.world.World;
import opekope2.lilac.api.tick.ITickNotifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BookScreen.class)
public abstract class BookScreenMixin {
    @Unique
    private static final ITickNotifier tickNotifier = ITickNotifier.getInstance();

    @Unique
    private static void triggerTick() {
        World world = MinecraftClient.getInstance().world;
        if (world != null) {
            tickNotifier.tick(world);
        }
    }

    @Inject(method = "setPage", at = @At("RETURN"))
    private void setPageMixin(int index, CallbackInfoReturnable<Boolean> cir) {
        triggerTick();
    }

    @Inject(method = "goToNextPage", at = @At("RETURN"))
    private void goToNextPageMixin(CallbackInfo ci) {
        triggerTick();
    }

    @Inject(method = "goToPreviousPage", at = @At("RETURN"))
    private void goToPreviousPageMixin(CallbackInfo ci) {
        triggerTick();
    }
}
