package opekope2.optigui.internal

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
import opekope2.optigui.util.collections.EnumObjectPairMutableSet
import java.util.*

internal object TextStyler {
    private var prevFilter = TextureChangerFilter.NO_OP

    private var stringCache = Cache<String, FormattedCharSequence>()
    private var prevStringCache = Cache<String, FormattedCharSequence>()
    private var textCache = Cache<Component, Component>()
    private var prevTextCache = Cache<Component, Component>()

    val renderedStrings = EnumObjectPairMutableSet<TextOrigin, String>(TextOrigin::class.java)
    val renderedTexts = EnumObjectPairMutableSet<TextOrigin, Component>(TextOrigin::class.java)

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
        renderedStrings.add(origin, text)

        if (TextureChanger.filter.textStyleChangers.isEmpty()) return null
        if (stringCache.contains(text, origin)) return stringCache.getValue(text, origin)
        if (stringCache.containsIgnored(text, origin)) return null

        val styler = getStyler(text, origin, stringWithSourceCodec, stringCache) ?: return null
        return stringCache.getOrPut(text, origin) {
            prevStringCache[text, origin] ?: FormattedCharSequence.forward(text, styler.style)
        }
    }

    @JvmStatic
    fun styleText(text: Component, origin: TextOrigin): Component {
        if (!TextureChanger.renderingScreen) return text
        if (!InteractionManager.isInteracting) return text
        renderedTexts.add(origin, text)

        if (TextureChanger.filter.textStyleChangers.isEmpty()) return text
        if (textCache.contains(text, origin)) return textCache.getValue(text, origin)
        if (textCache.containsIgnored(text, origin)) return text

        val styler = getStyler(text, origin, textWithSourceCodec, textCache) ?: return text
        return textCache.getOrPut(text, origin) {
            prevTextCache[text, origin] ?: styler.applyStyleTo(text)
        }
    }

    private fun <TText : Any> getStyler(
        text: TText,
        origin: TextOrigin,
        textCodec: Codec<Pair<TText, TextOrigin>>,
        cache: Cache<TText, *>
    ): TextStyleChanger? {
        val encoded = textCodec.encodeStart(NbtOps.INSTANCE, Pair(text, origin))
        if (encoded.isError) {
            cache.ignore(text, origin)
            return null
        }

        val nbt = encoded.result().get()
        val styler = TextureChanger.filter.textStyleChangers.firstOrNull { it.filter.test(nbt, nbt) }
        if (styler != null) return styler

        cache.ignore(text, origin)
        return null
    }

    fun clearCache(disconnected: Boolean) {
        renderedStrings.clear()
        renderedTexts.clear()

        val filterChanged = prevFilter.textStyleChangers !== TextureChanger.filter.textStyleChangers
        prevFilter = TextureChanger.filter

        // Unlike interaction NBT, text codec is deterministic, so clearing the cache is only required if the filter changed
        if (!filterChanged && !disconnected) return

        if (disconnected) prevStringCache.clear()
        else stringCache = prevStringCache.also { prevStringCache = stringCache }
        stringCache.clear()

        if (disconnected) prevTextCache.clear()
        else textCache = prevTextCache.also { prevTextCache = textCache }
        textCache.clear()
    }

    private class Cache<TKey : Any, TValue> {
        private val cache: EnumMap<TextOrigin, MutableMap<TKey, TValue>> =
            EnumMap(TextOrigin.entries.associateWith { mutableMapOf() })
        private val ignored = EnumObjectPairMutableSet<TextOrigin, TKey>(TextOrigin::class.java)

        fun contains(text: TKey, origin: TextOrigin) = text in cache[origin]!!

        operator fun get(text: TKey, origin: TextOrigin): TValue? = cache[origin]!![text]

        fun getValue(text: TKey, origin: TextOrigin): TValue = cache[origin]!!.getValue(text)

        inline fun getOrPut(text: TKey, origin: TextOrigin, defaultValue: () -> TValue) =
            cache[origin]!!.getOrPut(text, defaultValue)

        fun ignore(text: TKey, origin: TextOrigin) = ignored.add(origin, text)

        fun containsIgnored(text: TKey, origin: TextOrigin) = ignored.contains(origin, text)

        fun clear() {
            cache.values.forEach(MutableMap<*, *>::clear)
            ignored.clear()
        }
    }
}
