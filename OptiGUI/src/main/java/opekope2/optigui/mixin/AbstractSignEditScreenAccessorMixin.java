package opekope2.optigui.mixin;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AbstractSignEditScreen.class)
public interface AbstractSignEditScreenAccessorMixin {
    @Accessor
    SignBlockEntity getBlockEntity();
}
