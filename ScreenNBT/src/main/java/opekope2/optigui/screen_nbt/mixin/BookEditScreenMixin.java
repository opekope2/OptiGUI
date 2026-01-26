package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import opekope2.optigui.screen_api.util.INbtConvertible;
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
    public CompoundTag optiGui_asNbt(RegistryAccess registryAccess) {
        var compound = INbtConvertible.super.optiGui_asNbt(registryAccess);
        compound.putInt(CURRENT_PAGE_KEY, getCurrentPage() + 1);
        compound.putInt(NUM_PAGES_KEY, getNumPages());
        return compound;
    }
}
