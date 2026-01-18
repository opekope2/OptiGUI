package opekope2.optigui.util.dfu

import com.mojang.datafixers.util.Either
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.Decoder
import com.mojang.serialization.DynamicOps

/**
 * Similar to [Codec.withAlternative], but it also tries to encode an object using both codecs, whichever is successful.
 *
 * @param first One of the codecs
 * @param second The other codec
 */
class EitherCodec<A>(val first: Codec<A>, val second: Codec<A>) : Codec<A> {
    private val decoder: Decoder<Either<A, A>> = Codec.either(first, second)

    override fun <T> encode(input: A, ops: DynamicOps<T>, prefix: T): DataResult<T> {
        val firstRead = first.encode(input, ops, prefix)
        if (firstRead.isSuccess) return firstRead

        val secondRead = second.encode(input, ops, prefix)
        if (secondRead.isSuccess) return secondRead

        if (firstRead.hasResultOrPartial()) return firstRead
        if (secondRead.hasResultOrPartial()) return secondRead

        return DataResult.error {
            val firstError = firstRead.error().orElseThrow().message()
            val secondError = secondRead.error().orElseThrow().message()
            "Failed to encode either. First: $firstError; Second: $secondError"
        }
    }

    override fun <T> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<A, T>> =
        decoder.decode(ops, input).map { it.mapFirst(Either<*, *>::unwrap) }
}
