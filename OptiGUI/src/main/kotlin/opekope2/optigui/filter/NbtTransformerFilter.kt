package opekope2.optigui.filter

import com.mojang.serialization.Codec
import net.minecraft.nbt.Tag
import opekope2.optigui.filter.transformer.INbtTransformer
import opekope2.optigui.filter.transformer.IPrefixNbtTransformer
import opekope2.optigui.util.NbtFilterEvaluation

/**
 * An NBT filter, which transforms an NBT element, and compares it with another NBT filter.
 *
 * @param subFilter The filter to test the transformed NBT element with
 * @param type The type describing this filter
 * @see INbtTransformer
 */
class NbtTransformerFilter(val subFilter: INbtFilter, override val type: IType) : INbtFilter {
    override fun test(nbt: Tag, root: Tag): Boolean {
        return subFilter.test(type.transformer.transform(nbt, root) ?: return false, root)
    }

    override fun testSubFilters(nbt: Tag?, root: Tag): List<NbtFilterEvaluation> {
        val transformed = if (nbt != null) type.transformer.transform(nbt, root) else null
        return listOf(NbtFilterEvaluation(subFilter, transformed, root))
    }

    /**
     * The base type describing [NbtTransformerFilter].
     *
     * @see Type
     * @see PrefixType
     */
    sealed interface IType : INbtFilter.IType<NbtTransformerFilter> {
        override val codec: Codec<NbtTransformerFilter>
            get() = INbtFilter.CODEC.xmap(
                { NbtTransformerFilter(it, this) },
                NbtTransformerFilter::subFilter
            )

        /**
         * The NBT transformer used to transform the input NBT element.
         */
        val transformer: INbtTransformer
    }

    /**
     * A type describing an [NbtTransformerFilter].
     *
     * @param transformer The NBT transformer used to transform the input NBT element
     */
    data class Type(override val transformer: INbtTransformer) : IType {
        override val codec = super.codec
    }

    /**
     * A type describing a prefix [NbtTransformerFilter].
     *
     * @param T The type of the NBT transformer used to transform the input NBT element
     * @param key The prefixed key of a prefix NBT transformer
     * @param transformer The NBT transformer created by a prefix NBT transformer by specifying the `key` argument in
     *   [IPrefixNbtTransformer.transform]
     */
    data class PrefixType<T : INbtTransformer>(override val key: String, override val transformer: T) : IType {
        init {
            require(isRegistered) { "Prefix NBT filter is not registered" }
        }

        override val isRegistered: Boolean
            get() = key.isNotEmpty() && key[0] in IPrefixNbtTransformer

        override val codec = super.codec
    }
}
