package opekope2.optigui.screen_api.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.Widget;
import opekope2.optigui.screen_api.screen.ITextureChangeableScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin implements ITextureChangeableScreen {
    @Shadow
    protected int backgroundWidth;

    @Shadow
    protected int x;

    @Shadow
    protected int y;

    @Override
    public void optiGui_positionInspectorWidget(Widget inspectorButton) {
        inspectorButton.setPosition(
                x + backgroundWidth - inspectorButton.getWidth(),
                y - inspectorButton.getHeight()
        );
    }
}
