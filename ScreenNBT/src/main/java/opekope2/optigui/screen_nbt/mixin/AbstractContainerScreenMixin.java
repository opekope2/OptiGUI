package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.inventory.AbstractContainerMenu;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_nbt.util.NbtUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen implements INbtConvertible {
    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Shadow
    public abstract AbstractContainerMenu getMenu();

    @Override
    public void optiGui_writeNbt(CompoundTag compound, HolderLookup.Provider lookup) {
        compound.put(SCREEN_TITLE_KEY, NbtUtil.encode(getTitle(), ComponentSerialization.CODEC, lookup));
        if (getMenu() instanceof INbtConvertible nbtConvertible)
            nbtConvertible.optiGui_writeNbt(compound, lookup);
        if (this instanceof RecipeUpdateListener recipeBookProvider)
            compound.putBoolean("recipe_book_open", recipeBookProvider.getRecipeBookComponent().isVisible());
    }
}
