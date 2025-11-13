package opekope2.optigui.filter

import com.mojang.serialization.Codec
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import opekope2.optigui.filter.comparer.INbtComparer
import opekope2.optigui.filter.transformer.NbtTransformerChain
import java.util.*

/**
 * An NBT filter, which compares an NBT element with another NBT element extracted from the root NBT element.
 *
 * @param transformerChain The NBT transformer chain used to extract the other NBT element from the root NBT element
 * @param type The type describing this filter
 * @see INbtComparer
 * @see ConstantNbtComparerFilter
 */
class DynamicNbtComparerFilter(private val transformerChain: NbtTransformerChain, override val type: Type) :
    INbtFilter {
    override fun test(nbt: NbtElement, root: NbtElement): Boolean {
        val reference = transformerChain.transform(nbt, root) ?: return false
        return type.comparer.compare(nbt, reference) in type.acceptedResults
    }

    override fun asString() = super.asString() + " " + transformerChain.transformerChain
        .joinToString(prefix = "[", postfix = "]", transform = { NbtString.escape(it.key) })

    /**
     * A type describing a [DynamicNbtComparerFilter].
     *
     * @param comparer The NBT comparer used to compare NBT elements. The root NBT element is transformed using
     *   [DynamicNbtComparerFilter.transformerChain], and then passed as the right hand side argument to
     *   [INbtComparer.compare]
     * @param acceptedResults The accepted results of a comparison
     */
    data class Type(val comparer: INbtComparer, val acceptedResults: EnumSet<INbtComparer.ComparisonResult>) :
        INbtFilter.IType<DynamicNbtComparerFilter> {
        override val codec: Codec<DynamicNbtComparerFilter> = NbtTransformerChain.CODEC.xmap(
            { DynamicNbtComparerFilter(it, this) },
            DynamicNbtComparerFilter::transformerChain
        )
    }
}
