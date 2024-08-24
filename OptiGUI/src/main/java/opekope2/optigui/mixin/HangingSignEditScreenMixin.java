package opekope2.optigui.mixin;

import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen;
import opekope2.optigui.screen.IRetexturableScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HangingSignEditScreen.class)
public abstract class HangingSignEditScreenMixin implements IRetexturableScreen {
}
