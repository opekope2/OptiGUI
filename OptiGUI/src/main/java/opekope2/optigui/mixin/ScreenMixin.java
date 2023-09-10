package opekope2.optigui.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import opekope2.optigui.api.IOptiGuiApi;
import opekope2.optigui.api.interaction.IInspector;
import opekope2.optigui.internal.InspectorKeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Unique
    private static final IOptiGuiApi optiguiApi = IOptiGuiApi.getImplementation();
    @Unique
    private static final IInspector inspector = IInspector.getInstance();

    @Shadow
    protected MinecraftClient client;

    @Inject(method = "keyPressed", at = @At("TAIL"))
    void handleKeyPress(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        Screen thiz = (Screen) (Object) this;
        if (!optiguiApi.isScreenRetexturable(thiz)) return;

        if (!InspectorKeyBinding.getKeyBinding().matchesKey(keyCode, scanCode)) return;

        String inspection = inspector.inspectCurrentInteraction();
        if (inspection == null) return;

        client.keyboard.setClipboard(inspection);
        client.getToastManager().add(new Toast() {
            @Override
            public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
                TextRenderer textRenderer = manager.getClient().textRenderer;
                context.drawTexture(Toast.TEXTURE, 0, 0, 0, 0, Toast.super.getWidth(), Toast.super.getHeight());

                context.drawText(textRenderer, Text.translatable("optigui.toast.inspector.title"), 7, 7, 0xFF00FFFF, false);
                context.drawText(textRenderer, Text.translatable("optigui.toast.inspector.description"), 7, 18, 0xFFFFFFFF, false);
                return startTime >= 4000 * manager.getNotificationDisplayTimeMultiplier()
                        ? Toast.Visibility.HIDE
                        : Toast.Visibility.SHOW;
            }
        });
    }
}
