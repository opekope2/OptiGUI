package opekope2.optigui.filter

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.nbt.NbtElement
import opekope2.optigui.filter.transformer.NbtListIndexTransformer
import opekope2.optigui.filter.transformer.SubNbtTransformer
import opekope2.optigui.internal.I18n
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
    override fun test(nbt: NbtElement, root: NbtElement): Boolean {
        val operator = type.operator
        return if (filters.promoteFirst { operator shortCircuitsOn it.test(nbt, root) }) operator.shortCircuitResult
        else !operator.shortCircuitResult
    }

    override fun testSubFilters(nbt: NbtElement, root: NbtElement) = filters.map { NbtFilterEvaluation(it, nbt, root) }

    /**
     * A type describing an [AggregateFilter].
     *
     * @param operator The aggregate operator specifying how to combine the results of multiple filters
     */
    enum class Type(val operator: AggregateOperator) : INbtFilter.IType<AggregateFilter> {
        /**
         * An [AggregateFilter] type, which requires all filters to return `false`.
         */
        NONE_OF(AggregateOperator.NONE_OF),

        /**
         * An [AggregateFilter] type, which requires at least one filter to return `true`.
         */
        ANY_OF(AggregateOperator.ANY_OF),

        /**
         * An [AggregateFilter] type, which requires at least one filter to return `false`.
         */
        SOME_OF(AggregateOperator.SOME_OF),

        /**
         * An [AggregateFilter] type, which requires all filters to return `true`.
         */
        ALL_OF(AggregateOperator.ALL_OF),

        /**
         * An [AggregateFilter] type, which requires all filters to return `false`, and serializes to and from a map
         * instead of a list. The map format is the only way to serialize and deserialize [SubNbtTransformer] and
         * [NbtListIndexTransformer] filters.
         */
        JSON_OBJECT(AggregateOperator.ALL_OF) {
            override val codec: Codec<AggregateFilter> =
                Codec.dispatchedMap(INbtFilter.keyCodec) { INbtFilter.getType(it).codec }.xmap(
                    { AggregateFilter(LinkedMruCollection(it.values), this) },
                    { filter -> filter.filters.associateBy { INbtFilter.getKey(it.type) } }
                ).validate(::validate)

            private fun validate(filter: AggregateFilter): DataResult<AggregateFilter> {
                val missing = filter.filters.filter { !INbtFilter.containsType(it.type) }
                if (missing.isNotEmpty()) return DataResult.error {
                    I18n.OPTIGUI_VALIDATION_ERROR_NO_FILTER_TYPE.getTranslation(missing.joinToString { it.type.toString() })
                }

                val duplicates =
                    filter.filters.groupingBy { INbtFilter.getKey(it.type) }.eachCount().filter { it.value > 1 }.keys
                if (duplicates.isNotEmpty()) return DataResult.error {
                    I18n.OPTIGUI_VALIDATION_ERROR_DUPLICATE_FILTERS.getTranslation(duplicates.joinToString())
                }

                return DataResult.success(filter)
            }
        };

        override val codec: Codec<AggregateFilter> = INbtFilter.listCodec.xmap(
            { AggregateFilter(LinkedMruCollection(it), this) },
            { it.filters.toList() }
        )
    }

    companion object {
        /**
         * Matches only if no filters match in the collection. Matches if [filters][filters] [is empty][isEmpty].
         *
         * @param filters The filters to evaluate
         */
        @JvmStatic
        fun noneOf(filters: Collection<INbtFilter>) =
            AggregateFilter(LinkedMruCollection(filters), Type.NONE_OF)

        /**
         * Matches if at least 1 filter matches in the collection. Doesn't match if [filters][filters]
         * [is empty][isEmpty].
         *
         * @param filters The filters to evaluate
         */
        @JvmStatic
        fun anyOf(filters: Collection<INbtFilter>) =
            AggregateFilter(LinkedMruCollection(filters), Type.ANY_OF)

        /**
         * Matches if 0 or more, but not all filters match in the collection. Doesn't match if [filters][filters]
         * [is empty][isEmpty].
         *
         * @param filters The filters to evaluate
         */
        @JvmStatic
        fun someOf(filters: Collection<INbtFilter>) =
            AggregateFilter(LinkedMruCollection(filters), Type.SOME_OF)

        /**
         * Matches if every single filter matches in the collection. Matches if [filters][filters] [is empty][isEmpty].
         *
         * @param filters The filters to evaluate
         */
        @JvmStatic
        fun allOf(filters: Collection<INbtFilter>) =
            AggregateFilter(LinkedMruCollection(filters), Type.ALL_OF)
    }
}
