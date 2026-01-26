package opekope2.optigui.filter

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.nbt.Tag
import opekope2.optigui.filter.transformer.NbtListIndexTransformer
import opekope2.optigui.filter.transformer.SubNbtTransformer
import opekope2.optigui.util.AggregateOperator
import opekope2.optigui.util.NbtFilterEvaluation
import opekope2.optigui.util.collections.LinkedMruCollection

/**
 * An NBT filter aggregating multiple filters using an [AggregateOperator].
 *
 * @param filters A collection of sub-filters to evaluate
 * @param type The type describing this filter
 * @see NbtListFilter
 */
class AggregateFilter(val filters: LinkedMruCollection<INbtFilter>, override val type: Type) : INbtFilter {
    override fun test(nbt: Tag, root: Tag): Boolean {
        val operator = type.operator
        return if (filters.promoteFirst { operator shortCircuitsOn it.test(nbt, root) }) operator.shortCircuitResult
        else !operator.shortCircuitResult
    }

    override fun testSubFilters(nbt: Tag?, root: Tag) = filters.map { NbtFilterEvaluation(it, nbt, root) }

    override fun asString() = type.operator.toString()

    /**
     * A type describing an [AggregateFilter].
     *
     * @param operator The aggregate operator specifying how to combine the results of multiple filters
     */
    enum class Type(val operator: AggregateOperator) : INbtFilter.IType<AggregateFilter> {
        /**
         * An [AggregateFilter] type, which requires all filters to return `false`.
         */
        NONE_OF(AggregateOperator.NONE),

        /**
         * An [AggregateFilter] type, which requires at least one filter to return `true`.
         */
        ANY_OF(AggregateOperator.ANY),

        /**
         * An [AggregateFilter] type, which requires at least one filter to return `false`.
         */
        SOME_OF(AggregateOperator.SOME),

        /**
         * An [AggregateFilter] type, which requires all filters to return `true`.
         */
        ALL_OF(AggregateOperator.ALL),

        /**
         * An [AggregateFilter] type, which requires all filters to return `false`, and serializes to and from a map
         * instead of a list. The map format is the only way to serialize and deserialize [SubNbtTransformer] and
         * [NbtListIndexTransformer] filters.
         */
        JSON_OBJECT(AggregateOperator.ALL) {
            override val codec: Codec<AggregateFilter> =
                Codec.dispatchedMap(INbtFilter.TYPE_CODEC, INbtFilter.IType<*>::codec).xmap(
                    { AggregateFilter(LinkedMruCollection(it.values), this) },
                    { it.filters.associateBy(INbtFilter::type) }
                ).validate(::validate)

            private fun validate(filter: AggregateFilter): DataResult<AggregateFilter> {
                val missing = filter.filters.filter { !it.type.isRegistered }
                if (missing.isNotEmpty()) return DataResult.error { "Filter type not registered: " + missing.joinToString { it.type.toString() } }

                val duplicates = filter.filters.groupingBy { it.type.key }.eachCount().filter { it.value > 1 }.keys
                if (duplicates.isNotEmpty()) return DataResult.error { "Duplicate filters: " + duplicates.joinToString() }

                return DataResult.success(filter)
            }
        };

        override val codec: Codec<AggregateFilter> = INbtFilter.LIST_CODEC.xmap(
            { AggregateFilter(LinkedMruCollection(it), this) },
            { it.filters.toList() }
        )
    }
}
