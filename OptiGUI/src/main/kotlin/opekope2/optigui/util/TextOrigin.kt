package opekope2.optigui.util

import com.mojang.serialization.Codec
import net.minecraft.network.chat.Component
import net.minecraft.util.StringRepresentable

/**
 * Represents the origin of a rendered text.
 *
 * @param id The string representation of the text origin
 */
enum class TextOrigin(private val id: String) : StringRepresentable {
    /**
     * The text is the title of the active screen.
     */
    TITLE("screen/title"),

    /**
     * The text is the query text in the creative inventory search bar.
     */
    CREATIVE_INVENTORY_SEARCH_BOX("creative_inventory/search_box"),

    /**
     * The text is the item name on the anvil screen.
     */
    ANVIL_NAME_FIELD("anvil/name_field"),

    /**
     * The text is a [String] with unknown origin.
     */
    UNKNOWN_STRING("unknown/string"),

    /**
     * The text is a [Component] with unknown origin.
     */
    UNKNOWN_TEXT("unknown/text");

    override fun getSerializedName() = id

    companion object {
        /**
         * A codec for [TextOrigin].
         */
        @JvmField
        val CODEC: Codec<TextOrigin> = StringRepresentable.fromEnum(::values)

        /**
         * Returns the origin of the given text.
         *
         * @param text The text to get the origin of
         * @return [TITLE] if the given text is the title of the active screen, [UNKNOWN_TEXT] otherwise
         */
        @JvmStatic
        fun of(text: Component) =
            if (text === mc.screen?.title) TITLE
            else UNKNOWN_TEXT
    }
}
