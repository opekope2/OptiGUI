package opekope2.optigui.util.dfu

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.*
import dev.runefox.json.JsonNode
import dev.runefox.json.kt.*
import java.util.*
import java.util.Spliterator.*
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.UnaryOperator
import java.util.stream.Stream
import java.util.stream.StreamSupport

/**
 * JSON 5 pps for [JsonNode].
 */
@OptIn(ExperimentalKotlinJsonNodeApi::class)
object Json5Ops : DynamicOps<JsonNode> {
    private fun JsonNode?.takeIfNotNull() = this?.takeIf { !isNull }

    override fun empty() = JsonNull

    override fun emptyMap() = JsonObject()

    override fun emptyList() = JsonArray()

    override fun <U> convertTo(outOps: DynamicOps<U>, input: JsonNode): U = when {
        input.isObject -> convertMap(outOps, input)
        input.isArray -> convertList(outOps, input)
        input.isNull -> outOps.empty()
        input.isString -> outOps.createString(input.asString())
        input.isBoolean -> outOps.createBoolean(input.asBoolean())
        input.isNumber -> outOps.createNumeric(input.asNumber())
        else -> throw IllegalArgumentException("Don't know how to convert $input")
    }

    override fun getNumberValue(input: JsonNode) =
        if (input.isNumber) success(input.asNumber())
        else error { "Not a number: $input" }

    override fun createNumeric(i: Number) = JsonNumber(i)

    override fun getBooleanValue(input: JsonNode) =
        if (input.isBoolean) success(input.asBoolean())
        else error { "Not a boolean: $input" }

    override fun createBoolean(value: Boolean) = JsonBool(value)

    override fun getStringValue(input: JsonNode) =
        if (input.isString) success(input.asString())
        else error { "Not a string: $input" }

    override fun createString(value: String) = JsonString(value)

    override fun mergeToList(list: JsonNode, value: JsonNode) = when {
        list.isNull -> success(JsonArray(value))
        list.isArray -> success(list.copy().add(value))
        else -> error(list, { "mergeToList called with not a list: $list" })
    }

    override fun mergeToList(list: JsonNode, values: List<JsonNode>) = when {
        list.isNull -> success(JsonArray(values))
        list.isArray -> success(list.copy { values.forEach(it::add) })
        else -> error(list) { "mergeToList called with not a list: $list" }
    }

    override fun mergeToMap(map: JsonNode, key: JsonNode, value: JsonNode) = when {
        !key.isString -> error(map) { "Key is not a string: $key" }
        map.isNull -> success(JsonObject { it[key.asString()] = value })
        map.isObject -> success(map.copy { it[key.asString()] = value })
        else -> error(map) { "mergeToMap called with not a map: $map" }
    }

    override fun mergeToMap(map: JsonNode, values: MapLike<JsonNode>): DataResult<JsonNode> {
        val result = when {
            map.isNull -> JsonObject()
            map.isObject -> map.copy()
            else -> return error(map) { "mergeToMap called with not a map: $map" }
        }

        val missed = mutableListOf<JsonNode>()
        values.entries().forEach {
            val key = it.first
            if (key.isString) result[key.asString()] = it.second
            else missed.add(key)
        }

        return if (missed.isEmpty()) success(result)
        else error(result) { "Some keys are not strings: $missed" }
    }

    override fun getMapValues(input: JsonNode): DataResult<Stream<Pair<JsonNode, JsonNode?>>> =
        if (!input.isObject) error { "Not a JSON object: $input" }
        else success(input.entries.stream().map { (key, value) -> Pair(JsonString(key), value.takeIfNotNull()) })

    override fun getMapEntries(input: JsonNode): DataResult<Consumer<BiConsumer<JsonNode, JsonNode?>>> =
        if (!input.isObject) error { "Not a JSON object: $input" }
        else success(Consumer { consumer ->
            for ((key, value) in input.entries) consumer.accept(createString(key), value.takeIfNotNull())
        })

    override fun getMap(input: JsonNode): DataResult<MapLike<JsonNode>> =
        if (!input.isObject) error { "Not a JSON object: $input" }
        else success(
            object : MapLike<JsonNode> {
                override fun get(key: JsonNode) = get(key.asString())

                override fun get(key: String) = input[key].takeIfNotNull()

                override fun entries(): Stream<Pair<JsonNode, JsonNode>> =
                    input.entries.stream().map { (key, value) -> Pair(createString(key), value) }

                override fun toString() = "MapLike[$input]"
            }
        )

    override fun createMap(map: Stream<Pair<JsonNode, JsonNode>>): JsonNode =
        map.collect(JsonNode.objectCollector({ it.first.asString() }, Pair<*, JsonNode>::getSecond))

    override fun getStream(input: JsonNode): DataResult<Stream<JsonNode>> =
        if (!input.isArray) error { "Not a JSON array: $input" }
        else {
            val characteristics = ORDERED or SIZED or NONNULL or IMMUTABLE
            val spliterator = Spliterators.spliterator(input.iterator(), input.size.toLong(), characteristics)
            success(StreamSupport.stream(spliterator, false).map { it.takeIfNotNull() })
        }

    override fun getList(input: JsonNode): DataResult<Consumer<Consumer<JsonNode?>>> =
        if (!input.isArray) error { "Not a JSON array: $input" }
        else success(Consumer { consumer -> for (node in input) consumer.accept(node.takeIfNotNull()) })

    override fun createList(input: Stream<JsonNode>): JsonNode = input.collect(JsonNode.arrayCollector())

    override fun remove(input: JsonNode, key: String): JsonNode =
        if (input.isObject) input.copy().remove(key) else input

    override fun toString() = "JSON5"

    override fun listBuilder(): ListBuilder<JsonNode> = ArrayBuilder()

    override fun mapBuilder(): RecordBuilder<JsonNode> = ObjectBuilder()

    private class ArrayBuilder : ListBuilder<JsonNode> {
        private var builder = createBuilder()

        private fun createBuilder() = success(JsonArray(), Lifecycle.stable())

        override fun ops() = this@Json5Ops

        override fun add(value: JsonNode) = apply { builder = builder.map { it.add(value) } }

        override fun add(value: DataResult<JsonNode>) = apply { builder = builder.apply2stable(JsonNode::add, value) }

        override fun withErrorsFrom(result: DataResult<*>) =
            apply { builder = builder.flatMap { builder -> result.map { _ -> builder } } }

        override fun mapError(onError: UnaryOperator<String>) = apply { builder = builder.mapError(onError) }

        override fun build(prefix: JsonNode): DataResult<JsonNode> = builder.flatMap { builder ->
            when {
                prefix.isNull -> success(builder.copy(), Lifecycle.stable())
                prefix.isArray -> success(prefix.copy { builder.forEach(it::add) }, Lifecycle.stable())
                else -> error(prefix) { "Cannot append a list to not a list: $prefix" }
            }
        }.also { builder = createBuilder() }
    }

    private class ObjectBuilder : RecordBuilder.AbstractStringBuilder<JsonNode, JsonNode>(this) {
        override fun initBuilder() = JsonObject()

        override fun append(key: String, value: JsonNode, builder: JsonNode): JsonNode = builder.set(key, value)

        override fun build(builder: JsonNode, prefix: JsonNode): DataResult<JsonNode> = when {
            prefix.isNull -> success(builder)
            prefix.isObject -> success(prefix.copy { builder.forEachEntry(it::set) })
            else -> error(prefix) { "mergeToMap called with not a map: $prefix" }
        }
    }
}
