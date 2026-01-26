package opekope2.optigui.internal.inspector

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.DataResult
import com.mojang.serialization.RecordBuilder
import dev.runefox.json.JsonNode
import dev.runefox.json.kt.ExperimentalKotlinJsonNodeApi
import dev.runefox.json.kt.JsonNumber
import dev.runefox.json.kt.JsonObject
import dev.runefox.json.kt.copy
import net.minecraft.nbt.Tag
import net.minecraft.resources.DelegatingOps
import opekope2.optigui.filter.transformer.NbtTypeTransformer
import opekope2.optigui.util.dfu.Json5Ops
import opekope2.optigui.util.dfu.error
import opekope2.optigui.util.dfu.success
import java.nio.ByteBuffer
import java.util.stream.IntStream
import java.util.stream.LongStream
import java.util.stream.Stream
import kotlin.streams.asSequence

internal class JsonInspectorOps private constructor(private val withType: Boolean) : DelegatingOps<JsonNode>(Json5Ops) {
    private inline fun create(type: Byte, init: (JsonNode) -> Unit = {}) = JsonObject {
        if (withType) it["type"] = NbtTypeTransformer.transform(type)?.asString
        init(it)
    }

    private fun create(type: Byte, json: JsonNode) =
        if (withType) create(type) { it["="] = json }
        else json

    private fun create(json: JsonNode) = when {
        !json.isPrimitive -> json
        json.isBoolean -> create(Tag.TAG_BYTE, json)
        json.isString -> create(Tag.TAG_STRING, json)
        !json.isNumber -> json
        json.asNumber() is Byte -> create(Tag.TAG_BYTE, json)
        json.asNumber() is Short -> create(Tag.TAG_SHORT, json)
        json.asNumber() is Int -> create(Tag.TAG_INT, json)
        json.asNumber() is Long -> create(Tag.TAG_LONG, json)
        json.asNumber() is Float -> create(Tag.TAG_FLOAT, json)
        json.asNumber() is Double -> create(Tag.TAG_DOUBLE, json)
        else -> create(Tag.TAG_ANY_NUMERIC, json)
    }

    override fun createMap(map: Stream<Pair<JsonNode, JsonNode>>) = create(Tag.TAG_COMPOUND) {
        map.forEachOrdered { pair -> it["$${pair.first.asString()}"] = create(pair.second) }
    }

    override fun createBoolean(bl: Boolean): JsonNode = createByte(if (bl) 1 else 0)

    override fun createMap(map: Map<JsonNode, JsonNode>) = create(Tag.TAG_COMPOUND) {
        map.forEach { (key, value) -> it["$$key"] = create(value) }
    }

    fun createList(type: Byte, stream: Stream<JsonNode>) = create(type) {
        stream.asSequence().withIndex().forEach { (index, element) -> it["_$index"] = create(element) }
    }

    override fun createList(stream: Stream<JsonNode>) = createList(Tag.TAG_LIST, stream)

    override fun createByteList(buf: ByteBuffer) = create(Tag.TAG_BYTE_ARRAY) {
        for (i in 0 until buf.limit()) it["_$i"] = create(buf[i])
    }

    override fun createIntList(stream: IntStream) =
        createList(Tag.TAG_INT_ARRAY, stream.mapToObj(::JsonNumber))

    override fun createLongList(stream: LongStream) =
        createList(Tag.TAG_LONG_ARRAY, stream.mapToObj(::JsonNumber))

    override fun mapBuilder(): RecordBuilder<JsonNode> = JsonRecordBuilder()

    private inner class JsonRecordBuilder() : RecordBuilder.AbstractStringBuilder<JsonNode, JsonNode>(this) {
        override fun append(key: String, value: JsonNode, builder: JsonNode): JsonNode =
            builder.set("$$key", create(value))

        override fun initBuilder() = create(Tag.TAG_COMPOUND)

        @OptIn(ExperimentalKotlinJsonNodeApi::class)
        override fun build(builder: JsonNode, prefix: JsonNode): DataResult<JsonNode> = when {
            prefix.isNull -> success(builder)
            prefix.isObject -> success(prefix.copy { builder.forEachEntry(it::set) })
            else -> error(prefix) { "mergeToMap called with not a map: $prefix" }
        }
    }

    companion object {
        @JvmField
        val WITH_TYPE = JsonInspectorOps(true)

        @JvmField
        val WITHOUT_TYPE = JsonInspectorOps(false)
    }
}
