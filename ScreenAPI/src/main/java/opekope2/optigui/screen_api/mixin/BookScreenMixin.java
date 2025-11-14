package opekope2.optigui.screen_api.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Text;
import opekope2.optigui.screen_api.screen.ITextureChangeableScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BookScreen.class)
public abstract class BookScreenMixin extends Screen implements ITextureChangeableScreen {
    @Shadow
    @Final
    protected static int WIDTH;

    private BookScreenMixin(Text title) {
        super(title);
    }

    @Override
    public void optiGui_positionInspectorWidget(Widget inspectorButton) {
        inspectorButton.setPosition(width / 2 + WIDTH / 2, 2); // TODO FixBookGUI, Double Books, Scholar, ...
    }
}
