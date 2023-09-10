package opekope2.optigui.mixin;

import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HangingSignEditScreen.class)
public interface IHangingSignEditScreenMixin extends IAbstractSignEditScreenMixin {
}
