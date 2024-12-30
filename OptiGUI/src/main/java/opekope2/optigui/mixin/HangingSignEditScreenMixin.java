package opekope2.optigui.mixin;

import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import opekope2.optigui.screen.IRetexturableScreen;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HangingSignEditScreen.class)
public abstract class HangingSignEditScreenMixin implements IRetexturableScreen {
    @Override
    public void optiGui_writeNbt(@NotNull NbtCompound compound, RegistryWrapper.@NotNull WrapperLookup lookup) {
    }
}
