package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import opekope2.optigui.screen_nbt.util.NbtUtil;
import opekope2.optigui.util.INbtConvertible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BookScreen.class)
public abstract class BookScreenMixin extends Screen implements INbtConvertible {
    protected BookScreenMixin(Text title) {
        super(title);
    }

    @Accessor
    protected abstract int getPageIndex();

    @Invoker
    protected abstract int callGetPageCount();

    @Override
    public void optiGui_writeNbt(NbtCompound compound, RegistryWrapper.WrapperLookup lookup) {
        NbtUtil.encode(compound, SCREEN_TITLE_KEY, getTitle(), TextCodecs.CODEC, lookup);
        compound.putInt(CURRENT_PAGE_KEY, getPageIndex() + 1);
        compound.putInt(PAGE_COUNT_KEY, callGetPageCount());
    }
}
