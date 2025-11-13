package opekope2.optigui.screen_api.util;

import net.minecraft.core.HolderLookup;
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
    String PAGE_COUNT_KEY = "page_count";

    /**
     * Writes the object's content to {@code compound}.
     *
     * @param compound The output {@link CompoundTag} to write contents to
     * @param lookup   The registry lookup used to encode NBT
     */
    void optiGui_writeNbt(CompoundTag compound, HolderLookup.Provider lookup);
}
