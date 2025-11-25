package opekope2.optigui.filter.transformer

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.nbt.*
import net.minecraft.util.ExtraCodecs
import opekope2.optigui.filter.NbtTransformerFilter
import opekope2.optigui.internal.I18n

/**
 * An NBT transformer, which transforms the input NBT list to one of its elements specified dynamically.
 *
 * @see PrefixSubNbtTransformer
 * @see NbtListIndexTransformer
 */
object PrefixNbtListIndexTransformer : IPrefixNbtTransformer {
    override val type = object : IPrefixNbtTransformer.IType<NbtListIndexTransformer> {
        override val transformer: PrefixNbtListIndexTransformer
            get() = PrefixNbtListIndexTransformer

        override val filterTypeCodec: Codec<NbtTransformerFilter.PrefixType<NbtListIndexTransformer>> =
            ExtraCodecs.converter(NbtOps.INSTANCE).comapFlatMap(::parseType) { IntTag.valueOf(it.transformer.index) }

        private fun parseType(tag: Tag): DataResult<NbtTransformerFilter.PrefixType<NbtListIndexTransformer>> {
            val index = when (tag) {
                is NumericTag -> tag.asInt
                is StringTag -> tag.asString.toIntOrNull()
                else -> null
            } ?: return DataResult.error { I18n.OPTIGUI_VALIDATION_ERROR_NOT_A_NUMBER.getTranslation(tag.asString) }

            val type = NbtTransformerFilter.PrefixType("$key$index", NbtListIndexTransformer(index))
            return DataResult.success(type)
        }
    }

    override fun transform(key: Tag, nbt: Tag, root: Tag): Tag? {
        val index = when (key) {
            is NumericTag -> key.asInt
            is StringTag -> key.asString.toIntOrNull() ?: return null
            else -> return null
        }
        return getElement(nbt, index)
    }

    /**
     * Gets the [index]th (or `size+index`th if [index] is negative) NBT element from [nbt] if it is a [CollectionTag].
     */
    @JvmStatic
    fun getElement(nbt: Tag, index: Int) = when {
        nbt !is CollectionTag<*> -> null
        index in 0 until nbt.size -> nbt[index]
        index in -nbt.size until 0 -> nbt[index + nbt.size]
        else -> null
    }
}
