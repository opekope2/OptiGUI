package opekope2.optigui.mixin.screen;

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import opekope2.optigui.internal.TextStyler;
import opekope2.optigui.screen.ITextureChangeableScreen;
import opekope2.optigui.util.TextOrigin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> implements ITextureChangeableScreen {
    @Shadow
    @Final
    private static int TAB_HEIGHT;

    @Shadow
    private TextFieldWidget searchBox;

    public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Override
    public void optiGui_positionInspectorWidget(Widget inspectorButton) {
        inspectorButton.setPosition(
                x + backgroundWidth - inspectorButton.getWidth(),
                y - inspectorButton.getHeight() - TAB_HEIGHT
        );
    }

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
