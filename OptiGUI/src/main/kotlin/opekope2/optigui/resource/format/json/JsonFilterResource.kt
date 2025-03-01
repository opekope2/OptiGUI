package opekope2.optigui.resource.format.json

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.Decoder
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.nbt.NbtElement
import net.minecraft.util.Identifier
import net.minecraft.util.dynamic.Codecs
import opekope2.optigui.filter.*
import opekope2.optigui.internal.resource.matcher.NbtComparableFilter
import opekope2.optigui.util.unwrap

/**
 * Represents an OptiGUI JSON-based filter.
 *
 * @param containers The containers to change the GUI textures of
 * @param textures A map containing the original and the changed textures
 * @param loadFilter Raw representation of a filter determining if the resource should be loaded
 * @param filter Raw representation of a filter filtering an interaction NBT
 */
data class JsonFilterResource(
    val containers: Either<Identifier, List<Identifier>>,
    val textures: Map<Identifier, Identifier>,
    val loadFilter: JsonElement,
    val filter: JsonElement
) {
    fun testLoadFilter(nbt: NbtElement): DataResult<Boolean> {
        val loadFilter = decodeNbtFilter(loadFilter).unwrap { return DataResult.error { it.message() } }
        return DataResult.success(loadFilter.test(nbt))
    }

    fun createTextureChangerFilters(): DataResult<Collection<TextureChangerFilter>> {
        val containers = containers.map(::listOf) { it }
        val filter = decodeNbtFilter(filter).unwrap { return DataResult.error { it.message() } }
        val filters = containers.map { TextureChangerFilter(it, filter, textures) }

        return DataResult.success(filters)
    }

    private fun decodeNbtFilter(json: JsonElement) = NBT_FILTER_DECODER.parse(JsonOps.INSTANCE, json)

    companion object {
        /**
         * Key of [JsonFilterResource.containers] in a JSON object.
         *
         * @see JsonFilterResource.containers
         */
        const val CONTAINERS_KEY = "containers"

        /**
         * Key of [JsonFilterResource.textures] in a JSON object.
         *
         * @see JsonFilterResource.textures
         */
        const val TEXTURES_KEY = "textures"

        /**
         * Key of [JsonFilterResource.loadFilter] in a JSON object.
         *
         * @see JsonFilterResource.loadFilter
         */
        const val LOAD_FILTER_KEY = "if"

        /**
         * Key of [JsonFilterResource.filter] in a JSON object.
         *
         * @see JsonFilterResource.filter
         */
        const val FILTER_KEY = "match"

        /**
         * Codec for [JsonFilterResource].
         */
        @JvmField
        val CODEC: Codec<JsonFilterResource> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.either(Identifier.CODEC, Identifier.CODEC.listOf()).fieldOf(CONTAINERS_KEY)
                    .forGetter(JsonFilterResource::containers),
                Codec.unboundedMap(Identifier.CODEC, Identifier.CODEC).fieldOf(TEXTURES_KEY)
                    .forGetter(JsonFilterResource::textures),
                Codecs.JSON_ELEMENT.optionalFieldOf(LOAD_FILTER_KEY, JsonObject())
                    .forGetter(JsonFilterResource::loadFilter),
                Codecs.JSON_ELEMENT.optionalFieldOf(FILTER_KEY, JsonObject())
                    .forGetter(JsonFilterResource::filter)
            ).apply(instance, ::JsonFilterResource)
        }

        /**
         * Decoder for an [INbtFilter] from [JsonFilterResource.loadFilter] and [JsonFilterResource.filter].
         */
        @JvmField
        val NBT_FILTER_DECODER: Decoder<INbtFilter> = Codecs.JSON_ELEMENT.flatMap(::decodeJsonFilter)

        private fun decodeJsonFilter(rawFilter: JsonElement?, depth: Int = 0): DataResult<INbtFilter> {
            if (depth >= NbtElement.MAX_DEPTH) return DataResult.error { "Nesting too deep: $rawFilter" }

            return when (rawFilter) {
                is JsonObject -> decodeJsonObjectFilter(rawFilter, depth)
                is JsonArray -> decodeJsonArrayFilter(rawFilter, depth)
                else -> NbtComparableFilter.EQUAL_TO_DECODER.parse(JsonOps.INSTANCE, rawFilter)
            }
        }

        @Suppress("NOTHING_TO_INLINE") // Stack size
        private inline fun decodeJsonObjectFilter(obj: JsonObject, depth: Int): DataResult<INbtFilter> {
            val filters = mutableListOf<INbtFilter>()

            for ((key, value) in obj.asMap()) {
                filters += when {
                    key.startsWith('@') -> {
                        val subFilter = decodeJsonFilter(value, depth + 1).unwrap { return it }
                        SubNbtFilter(key.substring(1), subFilter)
                    }

                    key == "#none" -> matchNone(decodeJsonFilter(value, depth + 1).unwrap { return it })
                    key == "#any" -> matchAny(decodeJsonFilter(value, depth + 1).unwrap { return it })
                    key == "#some" -> matchSome(decodeJsonFilter(value, depth + 1).unwrap { return it })
                    key == "#all" -> matchAll(decodeJsonFilter(value, depth + 1).unwrap { return it })

                    key.startsWith('#') -> {
                        val subNbtKey = key.substring(1)
                        val subNbtIndex =
                            subNbtKey.toIntOrNull() ?: return DataResult.error { "Not a number: $subNbtKey" }
                        val subFilter = decodeJsonFilter(value, depth + 1).unwrap { return it }
                        NbtListIndexFilter(subNbtIndex, subFilter)
                    }

                    else -> {
                        if (key !in NbtMatcherRegistry) return DataResult.error { "No such matcher: $key" }
                        val decoder = NbtMatcherRegistry.getValue(key)
                        decoder.parse(JsonOps.INSTANCE, value).unwrap { return it }
                    }
                }
            }

            return DataResult.success(matchAllOf(filters))
        }

        @Suppress("NOTHING_TO_INLINE") // Stack size
        private inline fun decodeJsonArrayFilter(array: JsonArray, depth: Int): DataResult<INbtFilter> {
            val filters = array.map {
                decodeJsonFilter(it, depth + 1).unwrap { error -> return error }
            }

            return DataResult.success(matchAnyOf(filters))
        }
    }
}
