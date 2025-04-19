package opekope2.optigui.mixin.screen;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen;
import net.minecraft.client.gui.widget.Widget;
import opekope2.optigui.screen.ITextureChangeableScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HangingSignEditScreen.class)
public abstract class HangingSignEditScreenMixin extends AbstractSignEditScreen implements ITextureChangeableScreen {
    public HangingSignEditScreenMixin(SignBlockEntity blockEntity, boolean front, boolean filtered) {
        super(blockEntity, front, filtered);
    }

    @Override
    public void optiGui_positionInspectorWidget(Widget inspectorButton) {
        inspectorButton.setPosition(width / 2 + 100 - inspectorButton.getWidth(), 40 - inspectorButton.getHeight());
    }
}
