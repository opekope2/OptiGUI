package opekope2.optigui.mixin.screen;

import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import opekope2.optigui.internal.TextStyler;
import opekope2.optigui.util.TextOrigin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin {
    @Shadow
    private TextFieldWidget nameField;

    @Inject(method = "setup", at = @At("TAIL"))
    private void makeNameFieldStyleable(CallbackInfo ci) {
        if (nameField != null) nameField.setRenderTextProvider(this::styleNameField);
    }

    @Unique
    private OrderedText styleNameField(String string, int firstCharacterIndex) {
        var text = TextStyler.styleText(string, TextOrigin.ANVIL_NAME_FIELD);
        return text != null ? text : OrderedText.styledForwardsVisitedString(string, Style.EMPTY);
    }
}
