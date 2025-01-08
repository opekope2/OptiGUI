package opekope2.optigui.resource.format.json

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.Decoder
import com.mojang.serialization.JavaOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.nbt.NbtElement
import net.minecraft.util.Identifier
import net.minecraft.util.dynamic.Codecs
import opekope2.optigui.filter.*
import opekope2.optigui.internal.resource.matcher.NbtComparableFilter
import opekope2.optigui.resource.matcher.NbtMatcherRegistry
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
    val loadFilter: Any,
    val filter: Any
) {
    private fun process(): DataResult<ParsedFilters> {
        val containers = containers.map(::listOf) { it }
        val textures = textures
        val loadFilter =
            NBT_FILTER_DECODER.parse(JavaOps.INSTANCE, loadFilter).unwrap { return DataResult.error { it.message() } }
        val filter =
            NBT_FILTER_DECODER.parse(JavaOps.INSTANCE, filter).unwrap { return DataResult.error { it.message() } }
        val filters = containers.map { TextureChangerFilter(it, filter, textures) }

        return DataResult.success(ParsedFilters(filters, loadFilter))
    }

    /**
     * Represents the parsed filters from a JSON resource.
     *
     * @param filters The filters loaded from the JSON
     * @param loadTimeFilter The filter determining if [filters] should be loaded
     */
    data class ParsedFilters(val filters: Collection<TextureChangerFilter>, val loadTimeFilter: INbtFilter)

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
                Codecs.BASIC_OBJECT.optionalFieldOf(LOAD_FILTER_KEY, listOf<Any>())
                    .forGetter(JsonFilterResource::loadFilter),
                Codecs.BASIC_OBJECT.optionalFieldOf(FILTER_KEY, listOf<Any>())
                    .forGetter(JsonFilterResource::filter)
            ).apply(instance, ::JsonFilterResource)
        }

        /**
         * Decoder for an [INbtFilter] from [JsonFilterResource.loadFilter] and [JsonFilterResource.filter].
         */
        val NBT_FILTER_DECODER: Decoder<INbtFilter> = Codecs.BASIC_OBJECT.flatMap(::decodeFilter)

        /**
         * Decoder for [ParsedFilters].
         */
        @JvmField
        val PARSED_FILTER_DECODER: Decoder<ParsedFilters> = CODEC.flatMap(JsonFilterResource::process)

        private fun decodeFilter(rawFilter: Any?, depth: Int = 0): DataResult<INbtFilter> {
            if (depth >= NbtElement.MAX_DEPTH) return DataResult.error { "Nesting too deep: $rawFilter" }

            return when (rawFilter) {
                is Map<*, *> -> decodeMapFilter(rawFilter, depth)
                is List<*> -> decodeListFilter(rawFilter, depth)
                else -> NbtComparableFilter.EQUAL_TO_DECODER.parse(JavaOps.INSTANCE, rawFilter)
            }
        }

        @Suppress("NOTHING_TO_INLINE") // Stack size
        private inline fun decodeMapFilter(map: Map<*, *>, depth: Int): DataResult<INbtFilter> {
            val filters = mutableListOf<INbtFilter>()

            for ((key, value) in map) {
                if (key !is String) return DataResult.error { "Not a string: $key" }

                if (key.startsWith('@')) {
                    val subNbtKey = key.substring(1)
                    val subNbtIndex = subNbtKey.toIntOrNull()
                    val subFilter = decodeFilter(value, depth + 1).unwrap { return it }
                    val subNbtFilter = SubNbtFilter(key.substring(1), subFilter)

                    if (subNbtIndex != null) {
                        filters += matchAny(listOf(subNbtFilter, NbtListIndexFilter(subNbtIndex, subFilter)))
                    } else {
                        filters += subNbtFilter
                    }

                    continue
                }

                if (key !in NbtMatcherRegistry) return DataResult.error { "No such matcher: $key" }
                val decoder = NbtMatcherRegistry.getValue(key)
                val filter = decoder.parse(JavaOps.INSTANCE, value).unwrap { return it }
                filters += filter
            }

            return DataResult.success(matchAll(filters))
        }

        @Suppress("NOTHING_TO_INLINE") // Stack size
        private inline fun decodeListFilter(list: List<*>, depth: Int): DataResult<INbtFilter> {
            val filters = list.map {
                decodeFilter(it, depth + 1).unwrap { error -> return error }
            }

            return DataResult.success(matchAll(filters))
        }
    }
}
