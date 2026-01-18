package opekope2.optigui.internal

import com.google.common.collect.HashBasedTable
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import net.minecraft.nbt.NbtOps
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.util.FormattedCharSequence
import opekope2.optigui.filter.text_style_changer.TextStyleChanger
import opekope2.optigui.filter.texture_changer.TextureChangerFilter
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.util.TextOrigin
import opekope2.optigui.util.collections.getOrPut
import kotlin.jvm.optionals.getOrNull

internal object TextStyler {
    private val IGNORED_STRING = FormattedCharSequence { true }
    private val IGNORED_TEXT = Component.empty()

    private var prevFilter = TextureChangerFilter.NO_OP

    private var stringCache = HashBasedTable.create<String, TextOrigin, FormattedCharSequence>()
    private var textCache = HashBasedTable.create<Component, TextOrigin, Component>()

    val renderedStrings = HashBasedTable.create<TextOrigin, String, Unit>()
    val renderedTexts = HashBasedTable.create<TextOrigin, Component, Unit>()

    const val TEXT_KEY = "text"
    const val ORIGIN_KEY = "origin"

    val stringWithSourceCodec: Codec<Pair<String, TextOrigin>> =
        Codec.pair(Codec.STRING.fieldOf(TEXT_KEY).codec(), TextOrigin.CODEC.fieldOf(ORIGIN_KEY).codec())
    val textWithSourceCodec: Codec<Pair<Component, TextOrigin>> =
        Codec.pair(ComponentSerialization.CODEC.fieldOf(TEXT_KEY).codec(), TextOrigin.CODEC.fieldOf(ORIGIN_KEY).codec())

    @JvmStatic
    fun styleText(text: String, origin: TextOrigin): FormattedCharSequence? {
        if (!TextureChanger.renderingScreen) return null
        if (!InteractionManager.isInteracting) return null
        renderedStrings.put(origin, text, Unit)

        if (TextureChanger.filter.textStyleChangers.isEmpty()) return null
        if (stringCache.contains(text, origin)) return stringCache[text, origin].takeUnless { it === IGNORED_STRING }

        return when (val styler = getStyler(text, origin, stringWithSourceCodec)) {
            null -> stringCache.put(text, origin, IGNORED_STRING).let { null }
            else -> stringCache.getOrPut(text, origin) { FormattedCharSequence.forward(text, styler.style) }
        }
    }

    @JvmStatic
    fun styleText(text: Component, origin: TextOrigin): Component {
        if (!TextureChanger.renderingScreen) return text
        if (!InteractionManager.isInteracting) return text
        renderedTexts.put(origin, text, Unit)

        if (TextureChanger.filter.textStyleChangers.isEmpty()) return text
        if (textCache.contains(text, origin)) return textCache[text, origin].takeUnless { it === IGNORED_TEXT } ?: text

        return when (val styler = getStyler(text, origin, textWithSourceCodec)) {
            null -> textCache.put(text, origin, IGNORED_TEXT).let { text }
            else -> textCache.getOrPut(text, origin) { styler.applyStyleTo(text) }
        }
    }

    private fun <T : Any> getStyler(text: T, origin: TextOrigin, codec: Codec<Pair<T, TextOrigin>>): TextStyleChanger? {
        val nbt = codec.encodeStart(NbtOps.INSTANCE, Pair(text, origin)).result().getOrNull() ?: return null
        return TextureChanger.filter.textStyleChangers.firstOrNull { it.filter.test(nbt, nbt) }
    }

    fun clearCache(disconnected: Boolean) {
        renderedStrings.clear()
        renderedTexts.clear()

        val filterChanged = prevFilter.textStyleChangers !== TextureChanger.filter.textStyleChangers
        prevFilter = TextureChanger.filter

        // Unlike interaction NBT, text codec is deterministic, so clearing the cache is only required if the filter changed
        if (filterChanged || disconnected) {
            stringCache.clear()
            textCache.clear()
        }
    }
}
