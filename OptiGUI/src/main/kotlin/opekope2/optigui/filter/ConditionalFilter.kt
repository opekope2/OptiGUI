package opekope2.optigui.filter

import com.mojang.serialization.Codec
import net.minecraft.nbt.Tag
import opekope2.optigui.filter.ConditionalFilter.Companion.ALWAYS
import opekope2.optigui.filter.ConditionalFilter.Companion.NEVER

/**
 * An NBT filter, which matches any NBT only if [match] is `true`.
 *
 * @param match Whether to match any NBT or none
 */
class ConditionalFilter private constructor(val match: Boolean) : INbtFilter {
    override val type get() = TYPE

    override fun test(nbt: Tag, root: Tag) = match

    override fun asString() = super.asString() + " " + match

    companion object {
        @JvmField
        val CODEC: Codec<ConditionalFilter> = Codec.BOOL.xmap(::of, ConditionalFilter::match)

        /**
         * A type describing a [ConstantNbtComparerFilter] with this comparer and a codec that only accepts a boolean.
         */
        @JvmField
        val TYPE = INbtFilter.Type(ConditionalFilter::class.java, CODEC)

        /**
         * An NBT filter, which never matches.
         */
        @JvmField
        val NEVER = ConditionalFilter(false)

        /**
         * An NBT filter, which always matches.
         */
        @JvmField
        val ALWAYS = ConditionalFilter(true)

        /**
         * Gets the appropriate instance of [ConditionalFilter].
         *
         * @param match Whether to match any NBT or none
         * @return [NEVER] if [match] is `false`, [ALWAYS] if [match] is `true`
         */
        @JvmStatic
        fun of(match: Boolean) = if (match) ALWAYS else NEVER
    }
}
