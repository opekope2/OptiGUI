package opekope2.optigui.filter

import com.mojang.serialization.Codec
import net.minecraft.nbt.Tag
import opekope2.optigui.filter.transformer.INbtTransformer
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
     */
    interface IType : INbtFilter.IType<NbtTransformerFilter> {
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
     * A type describing an [NbtTransformerFilter], the default implementation of [IType].
     *
     * @param transformer The NBT transformer used to transform the input NBT element
     */
    data class Type(override val transformer: INbtTransformer) : IType {
        override val codec = super.codec
    }

    /**
     * The base type describing a prefixed [NbtTransformerFilter].
     */
    interface IPrefixType : IType, INbtFilter.IPrefixType<NbtTransformerFilter>
}
