package opekope2.optigui.filter

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.*
import net.minecraft.nbt.NbtElement
import opekope2.optigui.internal.I18n
import opekope2.optigui.internal.filter.NbtComparableFilter
import opekope2.optigui.registry.RegistryBase
import opekope2.optigui.util.DataResultAccumulator
import java.util.function.Predicate

/**
 * Functional interface for filtering [NbtElement]s.
 */
fun interface INbtFilter : Predicate<NbtElement> {
    override fun and(other: Predicate<in NbtElement>) = INbtFilter { test(it) && other.test(it) }

    override fun negate() = INbtFilter { !test(it) }

    override fun or(other: Predicate<in NbtElement>) = INbtFilter { test(it) || other.test(it) }

    /**
     * NBT filter registry.
     */
    companion object JsonDecoderRegistry : RegistryBase<String, Decoder<out INbtFilter>>() {
        /**
         * An instance of [INbtFilter], which matches for every NBT element.
         */
        @JvmField
        val ALWAYS_MATCH = INbtFilter { true }

        /**
         * An instance of [INbtFilter], which matches for no NBT elements.
         */
        @JvmField
        val NEVER_MATCH = INbtFilter { false }

        /**
         * A [Codec] for [INbtFilter], which can only decode. Useful for creating decoders for other types.
         */
        @JvmField
        val NON_ENCODING_CODEC: Codec<INbtFilter> = Codec.recursive("INbtFilter") { selfCodec ->
            Codec.withAlternative(
                Codec.withAlternative(
                    NonEncodingJsonObjectCodec(selfCodec),
                    selfCodec.listOf().xmap(FilterCollectionFilter::anyOf, FilterCollectionFilter::filters)
                ),
                NbtComparableFilter.NON_ENCODING_CODEC
            )
        }

        /**
         * A [Decoder] to decode [INbtFilter] from a JSON resource.
         */
        @JvmField
        val DECODER: Decoder<INbtFilter> = NON_ENCODING_CODEC

        override fun validateEntry(key: String, value: Decoder<out INbtFilter>) {
            super.validateEntry(key, value)
            require(!key.startsWith('@')) { "Key must not start with @" }
            require(!key.startsWith('#') || key == "#none" || key == "#any" || key == "#some" || key == "#all") { "Key must not start with #" }
        }
    }

    private class NonEncodingJsonObjectCodec(private val selfDecoder: Decoder<INbtFilter>) : Codec<INbtFilter> {
        override fun <T> encode(input: INbtFilter, ops: DynamicOps<T>, prefix: T): DataResult<T> =
            DataResult.error { "Cannot encode INbtFilter $input" }

        override fun <T> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<INbtFilter, T>> =
            ops.getMap(input).flatMap { decodeMap(ops, it) }.map { Pair.of(it, ops.empty()) }

        private fun <T> decodeMap(ops: DynamicOps<T>, map: MapLike<T>): DataResult<INbtFilter> = map.entries()
            .map { pair -> ops.getStringValue(pair.first).flatMap { decodeFilter(ops, it, pair.second) } }
            .collect(DataResultAccumulator.createCollector(FilterCollectionFilter::allOf))

        private fun <T> decodeFilter(ops: DynamicOps<T>, key: String, value: T): DataResult<INbtFilter> = when {
            key.startsWith('@') -> decodeSubNbtFilter(ops, key, value)
            key in JsonDecoderRegistry -> decodeNbtFilter(ops, key, value)
            key.startsWith('#') -> decodeListIndexFilter(ops, key, value)
            else -> DataResult.error { I18n.OPTIGUI_RP_LOADER_ERROR_NO_OPERATOR.getTranslation(key) }
        }

        private fun <T> decodeSubNbtFilter(ops: DynamicOps<T>, key: String, value: T): DataResult<INbtFilter> =
            selfDecoder.parse(ops, value).map { SubNbtFilter(key.substring(1), it) }

        private fun <T> decodeNbtFilter(ops: DynamicOps<T>, key: String, value: T): DataResult<INbtFilter> =
            getValue(key).parse(ops, value) as DataResult<INbtFilter>

        private fun <T> decodeListIndexFilter(ops: DynamicOps<T>, key: String, value: T): DataResult<INbtFilter> {
            val indexKey = key.substring(1)
            val index = indexKey.toIntOrNull() ?: return DataResult.error {
                I18n.OPTIGUI_RP_LOADER_ERROR_NOT_A_NUMBER.getTranslation(indexKey)
            }

            return selfDecoder.parse(ops, value).map { NbtListIndexFilter(index, it) }
        }
    }
}
