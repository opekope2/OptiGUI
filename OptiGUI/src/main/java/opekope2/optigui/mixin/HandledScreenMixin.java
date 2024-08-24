package opekope2.optigui.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import opekope2.optigui.screen.IRetexturableScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin implements IRetexturableScreen {
}
