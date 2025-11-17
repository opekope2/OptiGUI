package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_api.util.NbtUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BookEditScreen.class)
public abstract class BookEditScreenMixin extends Screen implements INbtConvertible {
    private BookEditScreenMixin(Component title) {
        super(title);
    }

    @Accessor
    abstract int getCurrentPage();

    @Shadow
    protected abstract int getNumPages();

    @Override
    public void optiGui_writeNbt(CompoundTag compound, HolderLookup.Provider lookup) {
        compound.put(SCREEN_TITLE_KEY, NbtUtil.encode(getTitle(), ComponentSerialization.CODEC, lookup));
        compound.putInt(CURRENT_PAGE_KEY, getCurrentPage() + 1);
        compound.putInt(NUM_PAGES_KEY, getNumPages());
    }
}
