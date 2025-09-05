package opekope2.optigui.util.dfu

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import opekope2.optigui.util.DataResultAccumulator
import kotlin.streams.asStream

/**
 * A codec, which serializes objects to a [String] delimited by any of [delimiters].
 *
 * @param delimiters Characters separating serialized objects in a string
 * @param codec The list codec of the object to serialize
 */
class DelimitedListCodec<TElement>(private val delimiters: CharArray, private val codec: Codec<List<TElement>>) :
    Codec<List<TElement>> {
    init {
        require(delimiters.isNotEmpty()) { "No delimiters were specified" }
    }

    override fun <T> encode(input: List<TElement>, ops: DynamicOps<T>, prefix: T): DataResult<T> =
        codec.encode(input, ops, prefix).flatMap(ops::getStream).flatMap { stream ->
            stream.map(ops::getStringValue).collect(DataResultAccumulator.createCollector { it })
                .map { ops.createString(it.joinToString(delimiters[0].toString())) }
        }

    override fun <T> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<List<TElement>, T>> =
        ops.getStringValue(input).flatMap { string ->
            val list = string.splitToSequence(*delimiters).map(ops::createString).asStream()
            codec.decode(ops, ops.createList(list))
        }
}
