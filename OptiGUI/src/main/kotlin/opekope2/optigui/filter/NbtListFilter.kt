package opekope2.optigui.filter

import com.mojang.serialization.Codec
import net.minecraft.nbt.AbstractNbtList
import net.minecraft.nbt.NbtElement
import opekope2.optigui.util.AggregateOperator
import opekope2.optigui.util.NbtFilterEvaluation

/**
 * An NBT filter, which tests a subfilter for an NBT list's elements, and combines the results using an
 * [AggregateOperator].
 *
 * @param filter The filter testing the NBT list
 * @param type The type describing this filter
 * @see AggregateFilter
 */
class NbtListFilter(val filter: INbtFilter, override val type: Type) : INbtFilter {
    override fun test(nbt: NbtElement, root: NbtElement): Boolean {
        if (nbt !is AbstractNbtList<*>) return false

        val operator = type.operator
        return if (nbt.any { operator shortCircuitsOn filter.test(it, root) }) operator.shortCircuitResult
        else !operator.shortCircuitResult
    }

    override fun testSubFilters(nbt: NbtElement, root: NbtElement): Collection<NbtFilterEvaluation> {
        if (nbt !is AbstractNbtList<*>) return emptyList()
        return nbt.map { NbtFilterEvaluation(filter, it, root) }
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
        NONE_OF_LIST(AggregateOperator.NONE_OF),

        /**
         * An [NbtListFilter] type, which requires the filter to return `true` for at least one NBT list element.
         */
        ANY_OF_LIST(AggregateOperator.ANY_OF),

        /**
         * An [NbtListFilter] type, which requires the filter to return `false` for at least one NBT list element.
         */
        SOME_OF_LIST(AggregateOperator.SOME_OF),

        /**
         * An [NbtListFilter] type, which requires the filter to return `true` for all NBT list elements.
         */
        ALL_OF_LIST(AggregateOperator.ALL_OF);

        override val codec: Codec<NbtListFilter> = INbtFilter.codec.xmap(
            { NbtListFilter(it, this) },
            NbtListFilter::filter
        )
    }
}
