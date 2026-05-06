package opekope2.optigui.screen_api.mixin;

import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import opekope2.optigui.screen_api.screen.ITextureChangeableScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HangingSignEditScreen.class)
public abstract class HangingSignEditScreenMixin extends AbstractSignEditScreen implements ITextureChangeableScreen {
    private HangingSignEditScreenMixin(SignBlockEntity blockEntity, boolean front, boolean filtered) {
        super(blockEntity, front, filtered);
    }

    @Override
    public void optiGui_positionInspectorWidget(LayoutElement inspectorButton) {
        inspectorButton.setPosition(width / 2 + 100 - inspectorButton.getWidth(), 40 - inspectorButton.getHeight());
    }
}
