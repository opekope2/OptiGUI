package opekope2.optigui.screen_api.mixin;

import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.network.chat.Component;
import opekope2.optigui.screen_api.screen.ITextureChangeableScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BookEditScreen.class)
public abstract class BookEditScreenMixin extends Screen implements ITextureChangeableScreen {
    @Shadow
    @Final
    private static int IMAGE_WIDTH;

    private BookEditScreenMixin(Component title) {
        super(title);
    }

    @Override
    public void optiGui_positionInspectorWidget(LayoutElement inspectorButton) {
        inspectorButton.setPosition(width / 2 + IMAGE_WIDTH / 2, 2);
    }
}
