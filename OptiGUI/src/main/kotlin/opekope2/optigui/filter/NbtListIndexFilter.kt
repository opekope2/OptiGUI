package opekope2.optigui.filter

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import opekope2.optigui.filter.transformer.NbtListIndexTransformer
import opekope2.optigui.internal.I18n

/**
 * A specialized filter for [NbtListIndexTransformer].
 *
 * @param subFilter The sub-filter testing an NBT element in an NBT list
 * @param type The type describing this filter
 */
// This class shouldn't need to exist, but I don't want to change #none/#any/#some/#all to none/any/some/all or something else
class NbtListIndexFilter(override val subFilter: NbtTransformerFilter, override val type: Type) : INbtListFilter,
    INbtFilter by subFilter {
    init {
        require(subFilter.type == type.type) { "Filter type mismatch: ${type.type} != ${subFilter.type}" }
    }

    /**
     * A wrapper type describing an [NbtListIndexFilter].
     *
     * @param type The wrapped type describing an [NbtListIndexFilter]
     */
    data class Type(val type: NbtListIndexTransformer.Type) : INbtListFilter.IType {
        /**
         * Creates a new [Type] by supplying a new instance of [NbtListIndexTransformer.Type].
         *
         * @param index The index in the NBT list. If it's negative, indexing starts from the back
         */
        constructor(index: Int) : this(NbtListIndexTransformer.Type(index))

        override val codec: Codec<INbtListFilter> = type.codec.flatComapMap({ NbtListIndexFilter(it, this) }, {
            when (val filter = it.subFilter) {
                is NbtTransformerFilter -> DataResult.success(filter)
                else -> DataResult.error(I18n.OPTIGUI_VALIDATION_ERROR_UNSUPPORTED_FILTER.supplyTranslation(it.subFilter))
            }
        })

        override val nonPrefixedKey: String
            get() = type.index.toString()
    }
}
