package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.block.entity.SignText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractSignEditScreen.class)
public abstract class AbstractSignEditScreenMixin extends Screen {
    protected AbstractSignEditScreenMixin(Text title) {
        super(title);
    }

    @Accessor
    protected abstract SignText getText();
}
