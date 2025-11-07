package opekope2.optigui.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import opekope2.optigui.internal.TextStyler;
import opekope2.optigui.internal.TextureChanger;
import opekope2.optigui.util.TextOrigin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = DrawContext.class, priority = 800)
public abstract class DrawContextMixin {
    @Shadow
    public abstract int drawText(TextRenderer textRenderer, OrderedText text, int x, int y, int color, boolean shadow);

    @ModifyVariable(
            method = "drawTexture(Lnet/minecraft/util/Identifier;IIIIIIIFFII)V",
            at = @At("HEAD"),
            index = 1,
            argsOnly = true
    )
    private Identifier changeTexture(@Nullable Identifier texture) {
        return texture != null ? TextureChanger.changeTexture(texture) : null;
    }

    @ModifyVariable(
            method = {
                    "drawGuiTexture(Lnet/minecraft/util/Identifier;IIIII)V",
                    "drawGuiTexture(Lnet/minecraft/util/Identifier;IIIIIIIII)V"
            },
            at = @At("HEAD"),
            index = 1,
            argsOnly = true
    )
    private Identifier changeSprite(@Nullable Identifier sprite) {
        return sprite != null ? TextureChanger.changeSprite(sprite) : null;
    }

    @Inject(
            method = "drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I",
            at = @At("HEAD"),
            cancellable = true
    )
    private void changeTextStyle(TextRenderer textRenderer, @Nullable String text, int x, int y, int color, boolean shadow, CallbackInfoReturnable<Integer> cir) {
        if (text == null) return;
        text = textRenderer.isRightToLeft() ? textRenderer.mirror(text) : text;

        var orderedText = TextStyler.styleText(text, TextOrigin.UNKNOWN_STRING);
        if (orderedText == null) return;

        var i = this.drawText(textRenderer, orderedText, x, y, color, shadow);
        cir.setReturnValue(i);
    }

    @ModifyVariable(
            method = {
                    "drawCenteredTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V",
                    "drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I"
            },
            at = @At("HEAD"),
            index = 2,
            argsOnly = true
    )
    private Text changeTextStyle(Text text) {
        return TextStyler.styleText(text, TextOrigin.of(text));
    }

    @ModifyVariable(method = "drawTextWrapped", at = @At("HEAD"), index = 2, argsOnly = true)
    private StringVisitable changeTextStyle(StringVisitable visitable) {
        return visitable instanceof Text text ? TextStyler.styleText(text, TextOrigin.of(text)) : visitable;
    }
}
