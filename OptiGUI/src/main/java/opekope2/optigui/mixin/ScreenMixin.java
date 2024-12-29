package opekope2.optigui.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import opekope2.optigui.internal.InitializerKt;
import opekope2.optigui.internal.interaction.Inspector;
import opekope2.optigui.screen.IRetexturableScreen;
import opekope2.optigui.toast.InspectorToast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow
    protected MinecraftClient client;

    @Inject(method = "keyPressed", at = @At("TAIL"))
    void inspectInteraction(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!(this instanceof IRetexturableScreen)) return;
        if (!InitializerKt.INSPECTOR_KEY_BINDING.matchesKey(keyCode, scanCode)) return;

        String inspection = Inspector.inspectInteraction();
        if (inspection == null) return;

        client.keyboard.setClipboard(inspection);
        client.getToastManager().add(new InspectorToast());
    }
}
