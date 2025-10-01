package opekope2.optigui.filter

import com.mojang.serialization.Codec
import net.minecraft.nbt.NbtElement
import opekope2.optigui.filter.comparer.INbtComparer
import java.util.*

/**
 * An NBT filter, which compares an NBT element with a constant value loaded from a JSON filter resource.
 *
 * @param value The constant value loaded from a JSON filter resource
 * @param type The type describing this filter
 * @see INbtComparer
 * @see DynamicNbtComparerFilter
 */
class ConstantNbtComparerFilter(val value: NbtElement, override val type: Type) : INbtFilter {
    override fun test(nbt: NbtElement, root: NbtElement) = type.comparer.compare(nbt, value) in type.acceptedResults

    /**
     * A type describing a [ConstantNbtComparerFilter].
     *
     * @param comparer The NBT comparer used to compare NBT elements. [ConstantNbtComparerFilter.value] is passed as the
     *   right hand side argument to [INbtComparer.compare]
     * @param acceptedResults The accepted results of a comparison
     * @param valueCodec The codec of the value in the JSON filter resource
     */
    data class Type(
        val comparer: INbtComparer,
        val acceptedResults: EnumSet<INbtComparer.ComparisonResult>,
        val valueCodec: Codec<NbtElement>
    ) : INbtFilter.IType<ConstantNbtComparerFilter> {
        override val codec: Codec<ConstantNbtComparerFilter> =
            valueCodec.xmap({ ConstantNbtComparerFilter(it, this) }, ConstantNbtComparerFilter::value)
    }
}
