package opekope2.optigui.util.dfu

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import opekope2.optigui.util.collections.sequence
import kotlin.streams.asSequence
import kotlin.streams.asStream

/**
 * A codec, which serializes objects to a [String] delimited by any of [delimiters].
 *
 * @param delimiters Characters separating serialized objects in a string
 * @param codec The list codec of the object to serialize
 */
class DelimitedListCodec<E>(private val delimiters: CharArray, private val codec: Codec<List<E>>) : Codec<List<E>> {
    init {
        require(delimiters.isNotEmpty()) { "No delimiters were specified" }
    }

    override fun <T> encode(input: List<E>, ops: DynamicOps<T>, prefix: T): DataResult<T> =
        codec.encode(input, ops, prefix).flatMap(ops::getStream)
            .flatMap { it.asSequence().map(ops::getStringValue).sequence() }
            .map { ops.createString(it.joinToString(delimiters[0].toString())) }

    override fun <T> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<List<E>, T>> =
        ops.getStringValue(input).flatMap { string ->
            val sequence = string.splitToSequence(*delimiters).map(ops::createString)
            codec.decode(ops, ops.createList(sequence.asStream()))
        }
}
