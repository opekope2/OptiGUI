package opekope2.optigui.resource.format.json

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.Decoder
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.nbt.NbtElement
import net.minecraft.util.Identifier
import net.minecraft.util.dynamic.Codecs
import opekope2.optigui.filter.*
import opekope2.optigui.internal.filter.NbtComparableFilter
import opekope2.optigui.operator.INbtOperator
import opekope2.optigui.resource.format.json.JsonFilterResource.Companion.CODEC1
import opekope2.optigui.resource.format.json.JsonFilterResource.Companion.CODEC2
import opekope2.optigui.util.i18n
import opekope2.optigui.util.mapMessage
import opekope2.optigui.util.unwrap

/**
 * Represents an OptiGUI JSON-based filter.
 *
 * @param inventories The identifiers of the blocks, entities, or items to change the GUI textures of
 * @param textureChanges A map containing the original and the changed textures
 * @param spriteChanges A map containing the original and the changed sprites
 * @param loadFilter Raw representation of a filter determining if the resource should be loaded
 * @param filter Raw representation of a filter filtering an interaction NBT
 */
data class JsonFilterResource(
    val inventories: Set<Identifier>,
    val textureChanges: Map<Identifier, Identifier>,
    val spriteChanges: Map<Identifier, Identifier>,
    val loadFilter: JsonElement,
    val filter: JsonElement
) {
    @Deprecated("For backward-compatibility only")
    private constructor(
        inventories: Set<Identifier>,
        textureChanges: Map<Identifier, Identifier>,
        loadFilter: JsonElement,
        filter: JsonElement
    ) : this(inventories, textureChanges, mapOf(), loadFilter, filter)

    fun testLoadFilter(nbt: NbtElement): DataResult<Boolean> {
        val loadFilter = decodeNbtFilter(loadFilter).unwrap { return it.mapMessage() }
        return DataResult.success(loadFilter.test(nbt))
    }

    fun createTextureChangerFilters(resourceId: Identifier): DataResult<Collection<TextureChangerFilter>> {
        val filter = decodeNbtFilter(filter).unwrap { return it.mapMessage() }
        val filters = inventories.map { TextureChangerFilter(it, resourceId, filter, textureChanges, spriteChanges) }

        return DataResult.success(filters)
    }

    private fun decodeNbtFilter(json: JsonElement) = NBT_FILTER_DECODER.parse(JsonOps.INSTANCE, json)

    companion object {
        /**
         * Key of [JsonFilterResource.inventories] in a JSON object.
         *
         * @see JsonFilterResource.inventories
         */
        const val INVENTORIES_KEY = "inventories"

        /**
         * Key of [JsonFilterResource.textureChanges] in a JSON object.
         *
         * @see JsonFilterResource.textureChanges
         */
        const val TEXTURE_CHANGES_KEY = "change_textures"

        /**
         * Key of [JsonFilterResource.spriteChanges] in a JSON object.
         *
         * @see JsonFilterResource.spriteChanges
         */
        const val SPRITE_CHANGES_KEY = "change_sprites"

        /**
         * Key of [JsonFilterResource.loadFilter] in a JSON object.
         *
         * @see JsonFilterResource.loadFilter
         */
        const val LOAD_FILTER_KEY = "load_if"

        /**
         * Key of [JsonFilterResource.filter] in a JSON object.
         *
         * @see JsonFilterResource.filter
         */
        const val FILTER_KEY = "match"

        /**
         * V1 codec for [JsonFilterResource].
         */
        @JvmField
        @Deprecated("For backward-compatibility only")
        val CODEC1: Codec<JsonFilterResource> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.withAlternative(
                    Identifier.CODEC.listOf().xmap(List<Identifier>::toSet, Set<Identifier>::toList),
                    Identifier.CODEC,
                    ::setOf
                ).fieldOf("containers").forGetter(JsonFilterResource::inventories),
                Codec.unboundedMap(Identifier.CODEC, Identifier.CODEC).fieldOf("textures")
                    .forGetter(JsonFilterResource::textureChanges),
                Codecs.JSON_ELEMENT.optionalFieldOf("if", JsonObject())
                    .forGetter(JsonFilterResource::loadFilter),
                Codecs.JSON_ELEMENT.optionalFieldOf("match", JsonObject())
                    .forGetter(JsonFilterResource::filter)
            ).apply(instance, ::JsonFilterResource)
        }

        /**
         * V2 codec for [JsonFilterResource].
         */
        @JvmField
        val CODEC2: Codec<JsonFilterResource> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.withAlternative(
                    Identifier.CODEC.listOf().xmap(List<Identifier>::toSet, Set<Identifier>::toList),
                    Identifier.CODEC,
                    ::setOf
                ).fieldOf(INVENTORIES_KEY).forGetter(JsonFilterResource::inventories),
                Codec.unboundedMap(Identifier.CODEC, Identifier.CODEC).optionalFieldOf(TEXTURE_CHANGES_KEY, mapOf())
                    .forGetter(JsonFilterResource::textureChanges),
                Codec.unboundedMap(Identifier.CODEC, Identifier.CODEC).optionalFieldOf(SPRITE_CHANGES_KEY, mapOf())
                    .forGetter(JsonFilterResource::spriteChanges),
                Codecs.JSON_ELEMENT.optionalFieldOf(LOAD_FILTER_KEY, JsonObject())
                    .forGetter(JsonFilterResource::loadFilter),
                Codecs.JSON_ELEMENT.optionalFieldOf(FILTER_KEY, JsonObject())
                    .forGetter(JsonFilterResource::filter)
            ).apply(instance, ::JsonFilterResource)
        }

        /**
         * Codec for [JsonFilterResource], unifying [CODEC1] and [CODEC2].
         */
        @JvmField
        val CODEC: Codec<JsonFilterResource> = Codec.withAlternative(CODEC2, CODEC1)

        /**
         * Decoder for an [INbtFilter] from [JsonFilterResource.loadFilter] and [JsonFilterResource.filter].
         */
        @JvmField
        val NBT_FILTER_DECODER: Decoder<INbtFilter> = Codecs.JSON_ELEMENT.flatMap(::decodeJsonFilter)

        private fun decodeJsonFilter(rawFilter: JsonElement?, depth: Int = 0): DataResult<INbtFilter> {
            if (depth >= NbtElement.MAX_DEPTH) return DataResult.error {
                i18n("optigui.rp_loader.error.nesting_too_deep", "Nesting too deep: %s", rawFilter.toString())
            }

            return when (rawFilter) {
                is JsonObject -> decodeJsonObjectFilter(rawFilter, depth)
                is JsonArray -> decodeJsonArrayFilter(rawFilter, depth)
                else -> NbtComparableFilter.EQUAL_TO.createFilter(JsonOps.INSTANCE, rawFilter)
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
                            subNbtKey.toIntOrNull() ?: return DataResult.error {
                                i18n("optigui.rp_loader.error.not_a_number", "Not a number: %s", subNbtKey)
                            }
                        val subFilter = decodeJsonFilter(value, depth + 1).unwrap { return it }
                        NbtListIndexFilter(subNbtIndex, subFilter)
                    }

                    else -> {
                        if (key !in INbtOperator.Registry) return DataResult.error {
                            i18n("optigui.rp_loader.error.no_operator", "No such operator: %s", key)
                        }
                        val matchOperator = INbtOperator.Registry.getValue(key)
                        matchOperator.createFilter(JsonOps.INSTANCE, value).unwrap { return it }
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
