package opekope2.optigui.screen_nbt.nbt_provider;

import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ComponentSerialization;
import opekope2.optigui.interaction.IInteraction;
import opekope2.optigui.interaction.nbt_provider.IInteractionNbtProvider;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_api.util.NbtUtil;

/**
 * Provides the screen NBT of an interaction.
 */
public class ScreenNbtProvider implements IInteractionNbtProvider {
    @Override
    public Tag get(IInteraction interaction, RegistryAccess registryAccess) {
        var screen = interaction.getScreen().optiGui_asScreen();
        var compound = new CompoundTag();

        compound.put(INbtConvertible.SCREEN_TITLE_KEY, NbtUtil.encode(screen.getTitle(), ComponentSerialization.CODEC, registryAccess));
        if (screen instanceof RecipeUpdateListener listener)
            compound.putBoolean("recipe_book_open", listener.getRecipeBookComponent().isVisible());
        if (screen instanceof MenuAccess<?> menuAccess && menuAccess.getMenu() instanceof INbtConvertible nbtConvertibleMenu)
            compound.merge(nbtConvertibleMenu.optiGui_asNbt(registryAccess));
        compound.merge(interaction.getScreen().optiGui_asNbt(registryAccess));

        return compound;
    }
}
