package opekope2.optigui.mixin.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import opekope2.optigui.interaction.InteractionManager;
import opekope2.optigui.screen.ITextureChangeableScreen;
import opekope2.optigui.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BookScreen.class)
public abstract class BookScreenMixin extends Screen implements ITextureChangeableScreen {
    @Shadow
    @Final
    protected static int WIDTH;

    protected BookScreenMixin(Text title) {
        super(title);
    }

    @Accessor
    protected abstract int getPageIndex();

    @Invoker
    protected abstract int callGetPageCount();

    @Inject(method = "setPage", at = @At("RETURN"))
    private void clearInteractionCacheAfterPageChange(CallbackInfoReturnable<Boolean> cir) {
        InteractionManager.clearCache();
    }

    @Inject(method = {"goToNextPage", "goToPreviousPage"}, at = @At("RETURN"))
    private void clearInteractionCacheAfterPageChange(CallbackInfo ci) {
        InteractionManager.clearCache();
    }

    @Override
    public void optiGui_positionInspectorWidget(Widget inspectorButton) {
        inspectorButton.setPosition(width / 2 + WIDTH / 2, 2); // TODO FixBookGUI, Double Books, Scholar, ...
    }

    @Override
    public void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup) {
        ITextureChangeableScreen.super.optiGui_writeNbt(compound, lookup);

        compound.putInt(Constants.CURRENT_PAGE_KEY, getPageIndex() + 1);
        compound.putInt(Constants.PAGE_COUNT_KEY, callGetPageCount());
    }
}
