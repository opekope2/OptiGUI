package opekope2.optigui.mixin;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
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
    private EditBox name;

    @Inject(method = "subInit", at = @At("TAIL"))
    private void makeNameStyleable(CallbackInfo ci) {
        name.setFormatter(this::optiGui_styleName);
    }

    @Unique
    private FormattedCharSequence optiGui_styleName(String string, int firstCharacterIndex) {
        var text = TextStyler.styleText(string, TextOrigin.ANVIL_NAME_FIELD);
        return text != null ? text : FormattedCharSequence.forward(string, Style.EMPTY);
    }
}
