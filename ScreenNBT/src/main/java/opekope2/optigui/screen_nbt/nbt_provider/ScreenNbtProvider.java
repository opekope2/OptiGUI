package opekope2.optigui.screen_nbt.nbt_provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import opekope2.optigui.interaction.IInteraction;
import opekope2.optigui.interaction.nbt_provider.IInteractionNbtProvider;
import opekope2.optigui.screen_api.util.INbtConvertible;
import org.jetbrains.annotations.Nullable;

/**
 * Provides the screen NBT of an interaction.
 */
public class ScreenNbtProvider implements IInteractionNbtProvider {
    @Override
    public @Nullable Tag get(IInteraction interaction, HolderLookup.Provider lookup) {
        if (interaction.getScreen() instanceof INbtConvertible nbtConvertible) {
            var compound = new CompoundTag();
            nbtConvertible.optiGui_writeNbt(compound, lookup);
            return compound;
        }
        return null;
    }
}
