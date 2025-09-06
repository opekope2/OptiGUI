package opekope2.optigui.filter

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.nbt.NbtElement
import opekope2.optigui.internal.I18n
import opekope2.optigui.resource.format.json.JsonFilterResource
import opekope2.optigui.util.AggregateOperator

/**
 * An NBT filter, which is represented by a JSON object. It matches only if every subfilter matches, and stores the key
 * of every filter for encoding purposes.
 *
 * @param filters The filters specified in the JSON object
 * @see FilterCollectionFilter
 * @see AggregateOperator.ALL_OF
 */
class JsonObjectNbtFilter(filters: Map<String, INbtFilter>) : INbtFilter, Iterable<INbtFilter> {
    private val filters: Map<String, INbtFilter> = buildMap {
        for ((key, value) in filters) {
            when {
                key in INbtFilter.JsonCodecRegistry -> put(key, value)
                key.startsWith('@') -> {
                    require(value is SubNbtFilter) { "Not a SubNbtFilter: $value" }
                    val subNbtKey = key.substring(1)
                    require(subNbtKey == value.subNbtKey) { "Expected subNbtKey '$subNbtKey', got '${value.subNbtKey}'" }
                    put(key, value)
                }

                key.startsWith('#') -> {
                    require(value is NbtListIndexFilter) { "Not a NbtListIndexFilter: $value" }
                    val index = key.substring(1)
                    require(index.toInt() == value.index) { "Expected index $index, got ${value.index}" }
                    put(key, value)
                }

                else -> throw IllegalArgumentException(I18n.OPTIGUI_RP_LOADER_ERROR_NO_FILTER.getTranslation(key))
            }
        }
    }

    override fun test(nbt: NbtElement) = filters.values.all { it.test(nbt) }

    override fun iterator() = filters.values.iterator()

    companion object {
        private val KEY_CODEC = Codec.STRING.validate { key ->
            when {
                key in INbtFilter.JsonCodecRegistry -> DataResult.success(key)
                key.startsWith('@') -> DataResult.success(key)
                key.startsWith('#') && key.substring(1).toIntOrNull() != null -> DataResult.success(key)
                key.startsWith('#') -> DataResult.error(
                    I18n.OPTIGUI_RP_LOADER_ERROR_NOT_A_NUMBER.supplyTranslation(key.substring(1))
                )

                else -> DataResult.error(I18n.OPTIGUI_RP_LOADER_ERROR_NO_FILTER.supplyTranslation(key))
            }
        }

        /**
         * A codec for [JsonObjectNbtFilter].
         */
        @JvmField
        val CODEC: Codec<JsonObjectNbtFilter> = Codec.dispatchedMap(KEY_CODEC, ::getCodec).xmap(
            {
                it.mapValues { (key, value) ->
                    when {
                        key in INbtFilter.JsonCodecRegistry -> value
                        key.startsWith('@') -> SubNbtFilter(key.substring(1), value)
                        key.startsWith('#') -> NbtListIndexFilter(key.substring(1).toInt(), value)
                        else -> value
                    }
                }
            },
            {
                it.mapValues { (key, value) ->
                    when {
                        key in INbtFilter.JsonCodecRegistry -> value
                        key.startsWith('@') -> (value as SubNbtFilter).subFilter
                        key.startsWith('#') -> (value as NbtListIndexFilter).subFilter
                        else -> value
                    }
                }
            }
        ).xmap(::JsonObjectNbtFilter, JsonObjectNbtFilter::filters)

        private fun getCodec(key: String) = when {
            key in INbtFilter.JsonCodecRegistry -> INbtFilter.getValue(key)
            key.startsWith('@') -> JsonFilterResource.FILTER_CODEC
            key.startsWith('#') -> JsonFilterResource.FILTER_CODEC
            else -> throw IllegalArgumentException(I18n.OPTIGUI_RP_LOADER_ERROR_NO_FILTER.getTranslation(key))
        }
    }
}
