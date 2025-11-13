package opekope2.optigui.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import opekope2.optigui.internal.TextStyler;
import opekope2.optigui.internal.TextureChanger;
import opekope2.optigui.util.TextOrigin;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GuiGraphics.class, priority = 800)
public abstract class GuiGraphicsMixin {
    @Shadow
    public abstract int drawString(Font textRenderer, FormattedCharSequence text, int x, int y, int color, boolean shadow);

    @ModifyVariable(
            method = "blit(Lnet/minecraft/resources/ResourceLocation;IIIIIIIFFII)V",
            at = @At("HEAD"),
            index = 1,
            argsOnly = true
    )
    private @Nullable ResourceLocation changeTexture(@Nullable ResourceLocation texture) {
        return texture != null ? TextureChanger.changeTexture(texture) : null;
    }

    @ModifyVariable(
            method = {
                    "blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIII)V",
                    "blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIIII)V"
            },
            at = @At("HEAD"),
            index = 1,
            argsOnly = true
    )
    private @Nullable ResourceLocation changeSprite(@Nullable ResourceLocation sprite) {
        return sprite != null ? TextureChanger.changeSprite(sprite) : null;
    }

    @Inject(
            method = "drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I",
            at = @At("HEAD"),
            cancellable = true
    )
    private void changeTextStyle(Font textRenderer, @Nullable String text, int x, int y, int color, boolean shadow, CallbackInfoReturnable<Integer> cir) {
        if (text == null) return;
        text = textRenderer.isBidirectional() ? textRenderer.bidirectionalShaping(text) : text;

        var orderedText = TextStyler.styleText(text, TextOrigin.UNKNOWN_STRING);
        if (orderedText == null) return;

        var i = this.drawString(textRenderer, orderedText, x, y, color, shadow);
        cir.setReturnValue(i);
    }

    @ModifyVariable(
            method = {
                    "drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V",
                    "drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I"
            },
            at = @At("HEAD"),
            index = 2,
            argsOnly = true
    )
    private Component changeTextStyle(Component text) {
        return TextStyler.styleText(text, TextOrigin.of(text));
    }

    @ModifyVariable(method = "drawWordWrap", at = @At("HEAD"), index = 2, argsOnly = true)
    private FormattedText changeTextStyle(FormattedText visitable) {
        return visitable instanceof Component text ? TextStyler.styleText(text, TextOrigin.of(text)) : visitable;
    }
}
