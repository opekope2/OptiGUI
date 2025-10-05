package opekope2.optigui.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import opekope2.optigui.internal.InitializerKt;
import opekope2.optigui.registry.RetexturableScreenRegistry;
import opekope2.optigui.toast.InspectorToast;
import opekope2.optigui.util.InteractionUtil;
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
    void handleKeyPress(KeyInput input, CallbackInfoReturnable<Boolean> cir) {
        Screen thiz = (Screen) (Object) this;
        if (!RetexturableScreenRegistry.contains(thiz)) return;

        if (!InitializerKt.INSPECTOR_KEY_BINDING.matchesKey(input)) return;

        String inspection = InteractionUtil.inspectInteraction();
        if (inspection == null) return;

        client.keyboard.setClipboard(inspection);
        client.getToastManager().add(new InspectorToast());
    }
}
