package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import opekope2.optigui.screen_api.util.INbtConvertible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BookViewScreen.class)
public abstract class BookViewScreenMixin extends Screen implements INbtConvertible {
    protected BookViewScreenMixin(Component title) {
        super(title);
    }

    @Accessor
    protected abstract int getCurrentPage();

    // Do not @Shadow, as subclasses calling it will crash the game
    @Invoker
    protected abstract int callGetNumPages();

    @Override
    public CompoundTag optiGui_asNbt(RegistryAccess registryAccess) {
        var compound = INbtConvertible.super.optiGui_asNbt(registryAccess);
        compound.putInt(CURRENT_PAGE_KEY, getCurrentPage() + 1);
        compound.putInt(NUM_PAGES_KEY, callGetNumPages());
        return compound;
    }
}
