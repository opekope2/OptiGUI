package opekope2.optigui.mixin.screen;

import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import opekope2.optigui.interaction.InteractionManager;
import opekope2.optigui.screen.IRetexturableScreen;
import opekope2.optigui.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BookEditScreen.class)
public abstract class BookEditScreenMixin implements IRetexturableScreen {
    @Accessor
    abstract int getCurrentPage();

    @Invoker
    abstract int callCountPages();

    @Inject(method = "changePage", at = @At("RETURN"))
    private void clearTextureReplacerCacheAfterPageChange(CallbackInfo ci) {
        InteractionManager.clearCache();
    }

    @Override
    public void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup) {
        IRetexturableScreen.super.optiGui_writeNbt(compound, lookup);

        compound.putInt(Constants.CURRENT_PAGE_KEY, getCurrentPage() + 1);
        compound.putInt(Constants.PAGE_COUNT_KEY, callCountPages());
    }
}
