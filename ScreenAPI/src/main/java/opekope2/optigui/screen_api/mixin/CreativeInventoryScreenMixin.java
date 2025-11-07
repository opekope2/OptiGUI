package opekope2.optigui.screen_api.mixin;

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import opekope2.optigui.screen.ITextureChangeableScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> implements ITextureChangeableScreen {
    @Shadow
    @Final
    private static int TAB_HEIGHT;

    private CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Override
    public void optiGui_positionInspectorWidget(Widget inspectorButton) {
        inspectorButton.setPosition(
                x + backgroundWidth - inspectorButton.getWidth(),
                y - inspectorButton.getHeight() - TAB_HEIGHT
        );
    }
}
