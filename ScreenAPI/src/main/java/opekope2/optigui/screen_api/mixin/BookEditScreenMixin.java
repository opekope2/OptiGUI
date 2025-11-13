package opekope2.optigui.screen_api.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Text;
import opekope2.optigui.screen_api.screen.ITextureChangeableScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BookEditScreen.class)
public abstract class BookEditScreenMixin extends Screen implements ITextureChangeableScreen {
    @Shadow
    @Final
    private static int WIDTH;

    private BookEditScreenMixin(Text title) {
        super(title);
    }

    @Override
    public void optiGui_positionInspectorWidget(Widget inspectorButton) {
        inspectorButton.setPosition(width / 2 + WIDTH / 2, 2);
    }
}
