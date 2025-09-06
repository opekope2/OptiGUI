package opekope2.optigui.filter

import com.google.common.collect.Iterators
import com.mojang.serialization.Codec
import net.minecraft.nbt.AbstractNbtList
import net.minecraft.nbt.NbtElement
import opekope2.optigui.resource.format.json.JsonFilterResource
import opekope2.optigui.util.AggregateOperator

/**
 * An NBT filter, which tests a subfilter for an NBT list's elements, and combines the results using an
 * [AggregateOperator].
 *
 * @param filter The filter testing the NBT list
 * @param operator The operator that describes the way to combine the results of [filter]
 * @see FilterCollectionFilter
 */
class NbtListFilter(val filter: INbtFilter, val operator: AggregateOperator) : INbtFilter,
    Iterable<INbtFilter> {
    override fun test(nbt: NbtElement): Boolean {
        if (nbt !is AbstractNbtList<*>) return false
        for (elem in nbt) {
            if (operator shortCircuitsOn filter.test(elem)) return operator.shortCircuitResult
        }
        return !operator.shortCircuitResult
    }

    override fun iterator(): Iterator<INbtFilter> = Iterators.forArray(filter)

    companion object {
        /**
         * Creates a codec of [NbtListFilter] for the given [AggregateOperator].
         *
         * @param operator An aggregate operator combining the results of an NBT list's elements
         */
        @JvmStatic
        fun codec(operator: AggregateOperator): Codec<NbtListFilter> =
            JsonFilterResource.FILTER_CODEC.xmap({ NbtListFilter(it, operator) }, NbtListFilter::filter)
    }
}
