package opekope2.optigui.screen_api.util;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;

/**
 * Represents an object, which can be written to an {@link CompoundTag}.
 */
public interface INbtConvertible {
    /**
     * NBT key for a screen's title.
     */
    String SCREEN_TITLE_KEY = "title";

    /**
     * NBT key for a hanging sign edit screen's text.
     */
    String SCREEN_TEXT_KEY = "text";

    /**
     * NBT key for redstone comparator output calculated from an inventory screen.
     */
    String COMPARATOR_OUTPUT_KEY = "comparator_output";

    /**
     * NBT key for an inventory screen's inventory.
     */
    String INVENTORY_KEY = "inventory";

    /**
     * NBT key for an inventory screen's result inventory.
     */
    String RESULT_INVENTORY_KEY = "result";

    /**
     * NBT key for a book screen's current page.
     */
    String CURRENT_PAGE_KEY = "current_page";

    /**
     * NBT key for a book screen's page count.
     */
    String NUM_PAGES_KEY = "num_pages";

    /**
     * Encodes the object's content to a {@link CompoundTag}
     *
     * @param registryAccess The registries of the world
     * @return The {@link CompoundTag} containing the encoded object (an empty compound tag by default)
     */
    default CompoundTag optiGui_asNbt(RegistryAccess registryAccess) {
        return new CompoundTag();
    }
}
