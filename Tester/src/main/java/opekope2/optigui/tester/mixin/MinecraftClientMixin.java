package opekope2.optigui.tester.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import opekope2.optigui.tester.Tester;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Function;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @SuppressWarnings("UnreachableCode")
    @Inject(method = "method_53528", at = @At("HEAD"), cancellable = true)
    private void onInitFinished(CallbackInfo ci) {
        if (Tester.isEnabled()) {
            Tester.loadTestWorld((MinecraftClient) (Object) this);
            ci.cancel();
        }
    }

    @Inject(method = "createInitScreens", at = @At("HEAD"), cancellable = true)
    private void dontCreateInitScreens(List<Function<Runnable, Screen>> list, CallbackInfo ci) {
        if (Tester.isEnabled()) {
            ci.cancel();
        }
    }
}
