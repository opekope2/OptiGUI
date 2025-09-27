package opekope2.optigui.mixin.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Text;
import opekope2.optigui.screen.ITextureChangeableScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen implements ITextureChangeableScreen {
    protected HandledScreenMixin(Text title) {
        super(title);
    }

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
