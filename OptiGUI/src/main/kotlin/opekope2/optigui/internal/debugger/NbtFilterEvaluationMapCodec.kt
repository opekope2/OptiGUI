package opekope2.optigui.internal.debugger

import com.mojang.serialization.*
import opekope2.optigui.util.NbtFilterEvaluation
import java.util.stream.Stream

internal object NbtFilterEvaluationMapCodec : MapCodec<NbtFilterEvaluation>() {
    private const val FILTER_KEY = "filter"
    private const val INPUT_KEY = "input"
    private const val MATCH_KEY = "match"
    private const val SUBFILTERS_KEY = "subFilters"

    private val listEncoder: Encoder<List<NbtFilterEvaluation>> =
        Codec.of(encoder(), Decoder.error("Cannot decode NbtFilterEvaluation")).listOf()

    override fun <T> encode(
        input: NbtFilterEvaluation,
        ops: DynamicOps<T>,
        prefix: RecordBuilder<T>
    ): RecordBuilder<T> = prefix
        .add(FILTER_KEY, ops.createString(input.filter.asString()))
        .add(INPUT_KEY, input.nbt?.asString?.let(ops::createString) ?: ops.empty())
        .add(MATCH_KEY, ops.createBoolean(input.test()))
        .add(SUBFILTERS_KEY, listEncoder.encodeStart(ops, input.testSubFilters()))

    override fun <T> keys(ops: DynamicOps<T>): Stream<T> =
        Stream.of(FILTER_KEY, INPUT_KEY, MATCH_KEY, SUBFILTERS_KEY).map(ops::createString)

    override fun <T> decode(ops: DynamicOps<T>, input: MapLike<T>): DataResult<NbtFilterEvaluation> =
        DataResult.error { "NbtFilterEvaluation decoding is not supported" }
}
