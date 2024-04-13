package opekope2.optigui.tester.mixin;

import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CreateWorldScreen.class)
public interface ICreateWorldScreenMixin {
    @Invoker
    void invokeCreateLevel();
}
