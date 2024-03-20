package opekope2.optigui.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import opekope2.optigui.inter.IScreen;
import opekope2.optigui.internal.InitializerKt;
import opekope2.optigui.registry.RetexturableScreenRegistry;
import opekope2.optigui.toast.InspectorToast;
import opekope2.optigui.util.InteractionUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class ScreenMixin implements IScreen {
    @Shadow
    protected MinecraftClient client;

    @Mutable
    @Shadow @Final
    protected Text title;//TODO title

    @Inject(method = "keyPressed", at = @At("TAIL"))
    void handleKeyPress(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        Screen thiz = (Screen) (Object) this;
        if (!RetexturableScreenRegistry.contains(thiz)) return;

        if (!InitializerKt.INSPECTOR_KEY_BINDING.matchesKey(keyCode, scanCode)) return;

        String inspection = InteractionUtil.inspectInteraction();
        if (inspection == null) return;

        client.keyboard.setClipboard(inspection);
        client.getToastManager().add(new InspectorToast());
    }

    @Override
    public void setTitle(Text title) {//TODO title
        this.title = title;
    }
}
