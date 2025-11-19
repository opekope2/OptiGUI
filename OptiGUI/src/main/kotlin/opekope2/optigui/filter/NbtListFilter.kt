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
class NbtListFilter(override val subFilter: INbtFilter, override val type: Type) : INbtListFilter {
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
    enum class Type(val operator: AggregateOperator, override val nonPrefixedKey: String) : INbtListFilter.IType {
        /**
         * An [NbtListFilter] type, which requires the filter to return `false` for all NBT list elements.
         */
        NONE_OF(AggregateOperator.NONE_OF, "none"),

        /**
         * An [NbtListFilter] type, which requires the filter to return `true` for at least one NBT list element.
         */
        ANY_OF(AggregateOperator.ANY_OF, "any"),

        /**
         * An [NbtListFilter] type, which requires the filter to return `false` for at least one NBT list element.
         */
        SOME_OF(AggregateOperator.SOME_OF, "some"),

        /**
         * An [NbtListFilter] type, which requires the filter to return `true` for all NBT list elements.
         */
        ALL_OF(AggregateOperator.ALL_OF, "all");

        override val codec: Codec<INbtListFilter> = INbtFilter.CODEC.xmap(
            { NbtListFilter(it, this) },
            INbtListFilter::subFilter
        )
    }
}
