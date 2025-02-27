package opekope2.optigui.mixin.screen;

import net.minecraft.block.entity.SignText;
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import opekope2.optigui.screen.ITextureChangeableScreen;
import opekope2.optigui.util.Constants;
import opekope2.optigui.util.NbtUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HangingSignEditScreen.class)
public abstract class HangingSignEditScreenMixin extends AbstractSignEditScreenMixin implements ITextureChangeableScreen {
    protected HangingSignEditScreenMixin(Text title) {
        super(title);
    }

    @Override
    public void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup) {
        ITextureChangeableScreen.super.optiGui_writeNbt(compound, lookup);
        NbtUtil.encode(compound, Constants.SCREEN_TEXT_KEY, getText(), SignText.CODEC, lookup);
    }
}
