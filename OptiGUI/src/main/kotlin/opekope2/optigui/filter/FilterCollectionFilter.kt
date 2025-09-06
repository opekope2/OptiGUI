package opekope2.optigui.filter

import com.mojang.serialization.Codec
import net.minecraft.nbt.NbtElement
import opekope2.optigui.resource.format.json.JsonFilterResource
import opekope2.optigui.util.AggregateOperator

/**
 * An NBT filter combining multiple filters using an [AggregateOperator].
 *
 * @param filters The sub-filters to evaluate
 * @param operator The operator that describes the way to combine the results of [filters]
 * @see NbtListFilter
 */
class FilterCollectionFilter(val filters: List<INbtFilter>, val operator: AggregateOperator) : INbtFilter,
    Iterable<INbtFilter> {
    override fun test(nbt: NbtElement): Boolean {
        for (subFilter in filters) {
            if (operator shortCircuitsOn subFilter.test(nbt)) return operator.shortCircuitResult
        }
        return true
    }

    override fun iterator() = filters.iterator()

    companion object {
        /**
         * Matches only if no filters match in the collection. Matches if [filters][filters] [is empty][isEmpty].
         *
         * @param filters The filters to evaluate
         */
        @JvmStatic
        fun noneOf(filters: Collection<INbtFilter>) =
            FilterCollectionFilter(filters.toList(), AggregateOperator.NONE_OF)

        /**
         * Matches if at least 1 filter matches in the collection. Doesn't match if [filters][filters]
         * [is empty][isEmpty].
         *
         * @param filters The filters to evaluate
         */
        @JvmStatic
        fun anyOf(filters: Collection<INbtFilter>) = FilterCollectionFilter(filters.toList(), AggregateOperator.ANY_OF)

        /**
         * Matches if 0 or more, but not all filters match in the collection. Doesn't match if [filters][filters]
         * [is empty][isEmpty].
         *
         * @param filters The filters to evaluate
         */
        @JvmStatic
        fun someOf(filters: Collection<INbtFilter>) =
            FilterCollectionFilter(filters.toList(), AggregateOperator.SOME_OF)

        /**
         * Matches if every single filter matches in the collection. Matches if [filters][filters] [is empty][isEmpty].
         *
         * @param filters The filters to evaluate
         */
        @JvmStatic
        fun allOf(filters: Collection<INbtFilter>) = FilterCollectionFilter(filters.toList(), AggregateOperator.ALL_OF)

        /**
         * Creates a codec of [FilterCollectionFilter] for the given [AggregateOperator].
         *
         * @param operator An aggregate operator combining the results of multiple filters
         */
        @JvmStatic
        fun codec(operator: AggregateOperator): Codec<FilterCollectionFilter> =
            JsonFilterResource.FILTER_CODEC.listOf()
                .xmap({ FilterCollectionFilter(it, operator) }, FilterCollectionFilter::filters)
    }
}
