package opekope2.optigui.filter

import com.mojang.serialization.Codec
import net.minecraft.nbt.CollectionTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import opekope2.optigui.util.AggregateOperator
import opekope2.optigui.util.NbtFilterEvaluation

/**
 * An NBT filter, which tests a subfilter for an NBT list's elements, and combines the results using an
 * [AggregateOperator].
 *
 * @param subFilter The filter testing the NBT list
 * @param type The type describing this filter
 * @see AggregateFilter
 */
class NbtListFilter(val subFilter: INbtFilter, override val type: Type) : INbtFilter {
    override fun test(nbt: Tag, root: Tag): Boolean {
        val operator = type.operator

        val shortCircuit = when (nbt) {
            is CompoundTag -> nbt.allKeys.any { operator shortCircuitsOn subFilter.test(nbt[it]!!, root) }
            is CollectionTag<*> -> nbt.any { operator shortCircuitsOn subFilter.test(it, root) }
            else -> return false
        }

        return if (shortCircuit) operator.shortCircuitResult
        else !operator.shortCircuitResult
    }

    override fun testSubFilters(nbt: Tag?, root: Tag) = when (nbt) {
        is CompoundTag -> nbt.allKeys.map { NbtFilterEvaluation(subFilter, nbt[it]!!, root) }
        is CollectionTag<*> -> nbt.map { NbtFilterEvaluation(subFilter, it, root) }
        else -> listOf(NbtFilterEvaluation(subFilter, null, root))
    }

    /**
     * A type describing an [NbtListFilter].
     *
     * @param operator The aggregate operator specifying how to combine the results of multiple filters
     */
    enum class Type(val operator: AggregateOperator) : INbtFilter.IType<NbtListFilter> {
        /**
         * An [NbtListFilter] type, which requires the filter to return `false` for all NBT list elements.
         */
        NONE(AggregateOperator.NONE_OF),

        /**
         * An [NbtListFilter] type, which requires the filter to return `true` for at least one NBT list element.
         */
        ANY(AggregateOperator.ANY_OF),

        /**
         * An [NbtListFilter] type, which requires the filter to return `false` for at least one NBT list element.
         */
        SOME(AggregateOperator.SOME_OF),

        /**
         * An [NbtListFilter] type, which requires the filter to return `true` for all NBT list elements.
         */
        ALL(AggregateOperator.ALL_OF);

        override val codec: Codec<NbtListFilter> = INbtFilter.CODEC.xmap(
            { NbtListFilter(it, this) },
            NbtListFilter::subFilter
        )
    }
}
