package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.block.entity.SignText;
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import opekope2.optigui.screen_nbt.util.NbtUtil;
import opekope2.optigui.util.INbtConvertible;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HangingSignEditScreen.class)
public abstract class HangingSignEditScreenMixin extends AbstractSignEditScreenMixin implements INbtConvertible {
    protected HangingSignEditScreenMixin(Text title) {
        super(title);
    }

    @Override
    public void optiGui_writeNbt(NbtCompound compound, RegistryWrapper.WrapperLookup lookup) {
        compound.put(SCREEN_TITLE_KEY, NbtUtil.encode(getTitle(), TextCodecs.CODEC, lookup));
        compound.put(SCREEN_TEXT_KEY, NbtUtil.encode(getText(), SignText.CODEC, lookup));
    }
}
