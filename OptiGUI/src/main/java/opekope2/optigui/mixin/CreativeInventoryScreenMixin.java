package opekope2.optigui.mixin;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import opekope2.optigui.internal.TextStyler;
import opekope2.optigui.util.TextOrigin;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin {
    @Shadow
    private @Nullable TextFieldWidget searchBox;

    @Inject(method = "init", at = @At("TAIL"))
    private void makeSearchBoxStyleable(CallbackInfo ci) {
        if (searchBox != null) searchBox.setRenderTextProvider(this::styleSearchBox);
    }

    @Unique
    private OrderedText styleSearchBox(String string, int firstCharacterIndex) {
        var text = TextStyler.styleText(string, TextOrigin.CREATIVE_INVENTORY_SEARCH_BOX);
        return text != null ? text : OrderedText.styledForwardsVisitedString(string, Style.EMPTY);
    }
}
