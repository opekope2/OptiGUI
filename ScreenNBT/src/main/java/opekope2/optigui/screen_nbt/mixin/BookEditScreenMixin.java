package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import opekope2.optigui.screen_nbt.util.NbtUtil;
import opekope2.optigui.util.INbtConvertible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BookEditScreen.class)
public abstract class BookEditScreenMixin extends Screen implements INbtConvertible {
    protected BookEditScreenMixin(Text title) {
        super(title);
    }

    @Accessor
    abstract int getCurrentPage();

    @Invoker
    abstract int callCountPages();

    @Override
    public void optiGui_writeNbt(NbtCompound compound, RegistryWrapper.WrapperLookup lookup) {
        NbtUtil.encode(compound, SCREEN_TITLE_KEY, getTitle(), TextCodecs.CODEC, lookup);
        compound.putInt(CURRENT_PAGE_KEY, getCurrentPage() + 1);
        compound.putInt(PAGE_COUNT_KEY, callCountPages());
    }
}
