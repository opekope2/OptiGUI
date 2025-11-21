package opekope2.optigui.filter

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.nbt.Tag
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
                Codec.dispatchedMap(INbtFilter.KEY_CODEC, ::getCodec).xmap(
                    { AggregateFilter(LinkedMruCollection(it.values), this) },
                    { filter -> filter.filters.associateBy { it.type.key } }
                ).validate(::validate)

            private fun getCodec(key: String) = when (key) {
                in INbtFilter.Registry -> INbtFilter.Registry.getValue(key)
                in INbtFilter.PrefixRegistry -> INbtFilter.PrefixRegistry.createType(key)
                else -> throw NoSuchElementException("Type is not registered: $key") // Shouldn't happen as INbtFilter.KEY_CODEC takes care of these
            }.codec

            private fun validate(filter: AggregateFilter): DataResult<AggregateFilter> {
                val missing = filter.filters.filter { !it.type.isRegistered }
                if (missing.isNotEmpty()) return DataResult.error {
                    I18n.OPTIGUI_VALIDATION_ERROR_NO_FILTER_TYPE.getTranslation(missing.joinToString { it.type.toString() })
                }

                val duplicates = filter.filters.groupingBy { it.type.key }.eachCount().filter { it.value > 1 }.keys
                if (duplicates.isNotEmpty()) return DataResult.error {
                    I18n.OPTIGUI_VALIDATION_ERROR_DUPLICATE_FILTERS.getTranslation(duplicates.joinToString())
                }

                return DataResult.success(filter)
            }
        };

        override val codec: Codec<AggregateFilter> = INbtFilter.LIST_CODEC.xmap(
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
