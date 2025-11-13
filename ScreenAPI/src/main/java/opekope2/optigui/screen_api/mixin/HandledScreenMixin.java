package opekope2.optigui.screen_api.mixin;

import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import opekope2.optigui.screen_api.screen.ITextureChangeableScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractContainerScreen.class)
public abstract class HandledScreenMixin implements ITextureChangeableScreen {
    @Shadow
    protected int imageWidth;

    @Shadow
    protected int leftPos;

    @Shadow
    protected int topPos;

    @Override
    public void optiGui_positionInspectorWidget(LayoutElement inspectorButton) {
        inspectorButton.setPosition(
                leftPos + imageWidth - inspectorButton.getWidth(),
                topPos - inspectorButton.getHeight()
        );
    }
}
