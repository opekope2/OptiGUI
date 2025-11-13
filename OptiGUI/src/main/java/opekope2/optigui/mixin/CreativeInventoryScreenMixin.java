package opekope2.optigui.mixin;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import opekope2.optigui.internal.TextStyler;
import opekope2.optigui.util.TextOrigin;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin {
    @Shadow
    private @Nullable EditBox searchBox;

    @Inject(method = "init", at = @At("TAIL"))
    private void makeSearchBoxStyleable(CallbackInfo ci) {
        if (searchBox != null) searchBox.setFormatter(this::optiGui_styleSearchBox);
    }

    @Unique
    private FormattedCharSequence optiGui_styleSearchBox(String string, int firstCharacterIndex) {
        var text = TextStyler.styleText(string, TextOrigin.CREATIVE_INVENTORY_SEARCH_BOX);
        return text != null ? text : FormattedCharSequence.forward(string, Style.EMPTY);
    }
}
