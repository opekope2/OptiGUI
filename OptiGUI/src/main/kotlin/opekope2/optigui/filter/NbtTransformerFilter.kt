package opekope2.optigui.filter

import com.mojang.serialization.Codec
import net.minecraft.nbt.NbtElement
import opekope2.optigui.filter.transformer.INbtTransformer
import opekope2.optigui.util.NbtFilterEvaluation

/**
 * An NBT filter, which transforms an NBT element, and compares it with another NBT filter.
 *
 * @param subFilter The filter to test the transformed NBT element with
 * @param type The type describing this filter
 * @see INbtTransformer
 */
class NbtTransformerFilter(val subFilter: INbtFilter, override val type: TypeBase) : INbtFilter {
    override fun test(nbt: NbtElement, root: NbtElement): Boolean {
        return subFilter.test(type.transformer.transform(nbt) ?: return false, root)
    }

    override fun testSubFilters(nbt: NbtElement?, root: NbtElement): List<NbtFilterEvaluation> {
        val transformed = nbt?.let(type.transformer::transform)
        return listOf(NbtFilterEvaluation(subFilter, transformed, root))
    }

    /**
     * The base type describing [NbtTransformerFilter].
     *
     * @param transformer The NBT transformer used to transform the input NBT element
     * @see Type
     */
    abstract class TypeBase(val transformer: INbtTransformer) : INbtFilter.IType<NbtTransformerFilter> {
        final override val codec: Codec<NbtTransformerFilter> by lazy {
            INbtFilter.codec.xmap(
                { NbtTransformerFilter(it, this) },
                NbtTransformerFilter::subFilter
            )
        }
    }

    /**
     * A type describing an [NbtTransformerFilter], the default implementation of [TypeBase].
     *
     * @param nbtTransformer The NBT transformer used to transform the input NBT element. It is identical to
     *   [transformer]
     */
    // nbtTransformer needed a new name because transformer is not virtual for performance reasons
    // But JVM only has INVOKEVIRTUAL, not INVOKE, hence how getTransformer() is called in NbtTransformerFilter::test
    data class Type(val nbtTransformer: INbtTransformer) : TypeBase(nbtTransformer)
}
