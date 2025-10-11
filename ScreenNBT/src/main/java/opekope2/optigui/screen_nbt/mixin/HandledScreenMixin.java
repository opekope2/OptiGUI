package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import opekope2.optigui.screen_nbt.util.NbtUtil;
import opekope2.optigui.util.INbtConvertible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen implements INbtConvertible {
    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Shadow
    public abstract ScreenHandler getScreenHandler();

    @Override
    public void optiGui_writeNbt(NbtCompound compound, RegistryWrapper.WrapperLookup lookup) {
        compound.put(SCREEN_TITLE_KEY, NbtUtil.encode(getTitle(), TextCodecs.CODEC, lookup));
        if (getScreenHandler() instanceof INbtConvertible nbtConvertible)
            nbtConvertible.optiGui_writeNbt(compound, lookup);
        if (this instanceof RecipeBookProvider recipeBookProvider)
            compound.putBoolean("recipe_book_open", recipeBookProvider.getRecipeBookWidget().isOpen());
    }
}
