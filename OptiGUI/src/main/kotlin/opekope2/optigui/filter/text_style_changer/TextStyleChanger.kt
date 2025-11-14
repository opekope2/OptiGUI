package opekope2.optigui.filter.text_style_changer

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.util.dfu.field

/**
 * Allows changing the style of a text if it matches a filter.
 *
 * @param filter The filter, which decides whether to change the style of a text
 * @param style The style to apply to matching texts
 * @param override Whether to completely change a text's style instead of changing only the unspecified parts
 */
data class TextStyleChanger(val filter: INbtFilter, val style: Style, val override: Boolean) {
    /**
     * Changes the style of the given text.
     *
     * @param text The text to change the style of
     * @return [text] itself with updated style
     */
    fun changeStyleOf(text: MutableText): MutableText =
        if (override) text.setStyle(style)
        else text.fillStyle(style)

    /**
     * Applies [style] to the given text.
     *
     * @param text The text to change the style of
     * @return A copy of [text] with changed style
     */
    fun applyStyleTo(text: Text): MutableText = changeStyleOf(text.copy())

    companion object {
        /**
         * Key of [TextStyleChanger.filter].
         *
         * @see TextStyleChanger.filter
         */
        const val FILTER_KEY = "match"

        /**
         * Key of [TextStyleChanger.style].
         *
         * @see TextStyleChanger.style
         */
        const val SET_STYLE_KEY = "style"

        /**
         * Key of [TextStyleChanger.override].
         *
         * @see TextStyleChanger.override
         */
        const val OVERRIDE_KEY = "override"

        /**
         * A codec for [TextStyleChanger].
         */
        @JvmField
        val CODEC: Codec<TextStyleChanger> = RecordCodecBuilder.create { instance ->
            instance.group(
                INbtFilter.CODEC.field(FILTER_KEY, TextStyleChanger::filter),
                Style.Codecs.CODEC.field(SET_STYLE_KEY, TextStyleChanger::style),
                Codec.BOOL.field(OVERRIDE_KEY, TextStyleChanger::override),
            ).apply(instance, ::TextStyleChanger)
        }
    }
}
