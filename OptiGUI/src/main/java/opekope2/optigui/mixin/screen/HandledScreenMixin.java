package opekope2.optigui.mixin.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import opekope2.optigui.screen.ITextureChangeableScreen;
import opekope2.optigui.util.INbtConvertible;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen implements ITextureChangeableScreen {
    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Shadow
    public abstract ScreenHandler getScreenHandler();

    @Shadow
    protected int backgroundWidth;

    @Shadow
    protected int x;

    @Shadow
    protected int y;

    @Override
    public void optiGui_positionInspectorWidget(Widget inspectorButton) {
        inspectorButton.setPosition(
                x + backgroundWidth - inspectorButton.getWidth(),
                y - inspectorButton.getHeight()
        );
    }

    @Override
    public void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup) {
        ITextureChangeableScreen.super.optiGui_writeNbt(compound, lookup);
        if (getScreenHandler() instanceof INbtConvertible nbtConvertible)
            nbtConvertible.optiGui_writeNbt(compound, lookup);
        if (this instanceof RecipeBookProvider recipeBookProvider)
            compound.putBoolean("recipe_book_open", recipeBookProvider.getRecipeBookWidget().isOpen());
    }
}
