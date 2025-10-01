package opekope2.optigui.internal.inspector

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.DataResult
import com.mojang.serialization.JsonOps
import com.mojang.serialization.RecordBuilder
import net.minecraft.nbt.NbtElement
import net.minecraft.util.dynamic.ForwardingDynamicOps
import opekope2.optigui.filter.transformer.NbtTypeTransformer
import java.nio.ByteBuffer
import java.util.stream.IntStream
import java.util.stream.LongStream
import java.util.stream.Stream
import kotlin.streams.asSequence

internal class JsonInspectorOps private constructor(private val withType: Boolean) :
    ForwardingDynamicOps<JsonElement>(JsonOps.INSTANCE) {
    fun create(type: Byte) = JsonObject().apply {
        if (withType) addProperty("type", NbtTypeTransformer.transform(type)?.asString())
    }

    fun create(type: Byte, json: JsonElement) =
        if (withType) create(type).apply { add("=", json) }
        else json

    fun create(json: JsonElement) = when {
        json !is JsonPrimitive -> json
        json.isBoolean -> create(NbtElement.BYTE_TYPE, json)
        json.isString -> create(NbtElement.STRING_TYPE, json)
        !json.isNumber -> json
        json.asNumber is Byte -> create(NbtElement.BYTE_TYPE, json)
        json.asNumber is Short -> create(NbtElement.SHORT_TYPE, json)
        json.asNumber is Int -> create(NbtElement.INT_TYPE, json)
        json.asNumber is Long -> create(NbtElement.LONG_TYPE, json)
        json.asNumber is Float -> create(NbtElement.FLOAT_TYPE, json)
        json.asNumber is Double -> create(NbtElement.DOUBLE_TYPE, json)
        else -> json
    }

    override fun createMap(map: Stream<Pair<JsonElement, JsonElement>>) = create(NbtElement.COMPOUND_TYPE).apply {
        map.forEachOrdered { pair -> add("@${pair.first.asString}", create(pair.second)) }
    }

    override fun createBoolean(bl: Boolean): JsonElement = createByte(if (bl) 1 else 0)

    override fun createMap(map: Map<JsonElement, JsonElement>) = create(NbtElement.COMPOUND_TYPE).apply {
        map.forEach { (key, value) -> add("@$key", create(value)) }
    }

    fun createList(type: Byte, stream: Stream<JsonElement>) = create(type).apply {
        stream.asSequence().withIndex().forEach { (index, element) -> add("#$index", create(element)) }
    }

    override fun createList(stream: Stream<JsonElement>) = createList(NbtElement.LIST_TYPE, stream)

    override fun createByteList(buf: ByteBuffer) = create(NbtElement.BYTE_ARRAY_TYPE).apply {
        for (i in 0 until buf.limit()) add("#$i", create(buf[i]))
    }

    override fun createIntList(stream: IntStream) =
        createList(NbtElement.INT_ARRAY_TYPE, stream.mapToObj(::JsonPrimitive))

    override fun createLongList(stream: LongStream) =
        createList(NbtElement.LONG_ARRAY_TYPE, stream.mapToObj(::JsonPrimitive))

    override fun mapBuilder(): RecordBuilder<JsonElement> = JsonRecordBuilder()

    private inner class JsonRecordBuilder() : RecordBuilder.AbstractStringBuilder<JsonElement, JsonObject>(this) {
        override fun append(key: String, value: JsonElement, builder: JsonObject) = builder.apply {
            add("@$key", create(value))
        }

        override fun initBuilder() = create(NbtElement.COMPOUND_TYPE)

        override fun build(builder: JsonObject, prefix: JsonElement?): DataResult<JsonElement?> {
            return when (prefix) {
                null, is JsonNull -> DataResult.success(builder)
                is JsonObject -> DataResult.success(JsonObject().apply {
                    for ((key, value) in prefix.asJsonObject.entrySet()) add(key, value)
                    for ((key, value) in builder.entrySet()) add(key, value)
                })

                else -> DataResult.error({ "mergeToMap called with not a map: $prefix" }, prefix)
            }
        }
    }

    companion object {
        @JvmField
        val WITH_TYPE = JsonInspectorOps(true)

        @JvmField
        val WITHOUT_TYPE = JsonInspectorOps(false)
    }
}
