package opekope2.optigui.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import opekope2.optigui.internal.InitializerKt;
import opekope2.optigui.registry.RetexturableScreenRegistry;
import opekope2.optigui.toast.InspectorToast;
import opekope2.optigui.util.InteractionUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Final
    @Shadow
    protected Minecraft minecraft;

    @Inject(method = "keyPressed", at = @At("TAIL"))
    void handleKeyPress(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        Screen thiz = (Screen) (Object) this;
        if (!RetexturableScreenRegistry.contains(thiz)) return;

        if (!InitializerKt.INSPECTOR_KEY_BINDING.matches(input)) return;

        String inspection = InteractionUtil.inspectInteraction();
        if (inspection == null) return;

        minecraft.keyboardHandler.setClipboard(inspection);
        minecraft.gui.toastManager().addToast(new InspectorToast());
    }
}
