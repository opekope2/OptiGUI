package opekope2.optigui.resource.format.json

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.entity.EntityType
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.util.dynamic.Codecs
import opekope2.optigui.filter.ConditionalFilter
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.filter.text_style_changer.TextStyleChanger
import opekope2.optigui.util.dfu.field
import opekope2.optigui.util.dfu.optionalField
import opekope2.optigui.util.dfu.toSet
import opekope2.optigui.util.identifier

/**
 * Represents an OptiGUI JSON filter resource.
 */
sealed class JsonFilterResource {
    /**
     * The format version of the JSON filter resource.
     */
    abstract val format: Int

    /**
     * Represents a version 1 OptiGUI JSON filter resource.
     *
     * @param containers The containers to change the GUI textures of
     * @param textures A map containing the original and the changed textures
     * @param loadFilter Raw representation of a filter determining if the resource should be loaded
     * @param filter Raw representation of a filter filtering an interaction NBT
     */
    data class V1(
        val containers: Set<Identifier>,
        val textures: Map<Identifier, Identifier>,
        val loadFilter: INbtFilter,
        val filter: INbtFilter
    ) : JsonFilterResource() {
        fun toV2() = V2(
            containers.filterTo(mutableSetOf(), Registries.BLOCK::containsId),
            containers.filterTo(mutableSetOf(), Registries.ENTITY_TYPE::containsId),
            containers.filterTo(mutableSetOf(), Registries.ITEM::containsId),
            EntityType.PLAYER.identifier in containers,
            false,
            textures.asSequence().filter { (key) -> key.path.endsWith(".png") }
                .map { (key, value) -> key to JsonTextureChanger(value) }.toMap(),
            textures.asSequence().filter { (key) -> !key.path.endsWith(".png") }
                .map { (key, value) -> key to JsonTextureChanger(value) }.toMap(),
            emptyList(),
            loadFilter,
            filter
        )

        override val format: Int
            get() = 1

        companion object {
            /**
             * Key of [V1.containers] in a JSON object.
             *
             * @see V1.containers
             */
            const val CONTAINERS_KEY = "containers"

            /**
             * Key of [V1.textures] in a JSON object.
             *
             * @see V1.textures
             */
            const val TEXTURES_KEY = "textures"

            /**
             * Key of [V1.loadFilter] in a JSON object.
             *
             * @see V1.loadFilter
             */
            const val LOAD_FILTER_KEY = "if"

            /**
             * Key of [V1.filter] in a JSON object.
             *
             * @see V1.filter
             */
            const val FILTER_KEY = "match"

            /**
             * A map codec for [JsonFilterResource.V1].
             */
            @JvmField
            val MAP_CODEC: MapCodec<V1> = RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    inventoryIdCodec().field(CONTAINERS_KEY, V1::containers),
                    Codec.unboundedMap(Identifier.CODEC, Identifier.CODEC).field(TEXTURES_KEY, V1::textures),
                    INbtFilter.codec.optionalField(LOAD_FILTER_KEY, V1::loadFilter, ConditionalFilter.ALWAYS),
                    INbtFilter.codec.optionalField(FILTER_KEY, V1::filter, ConditionalFilter.ALWAYS),
                ).apply(instance, ::V1)
            }
        }
    }

    /**
     * Represents a version 2 OptiGUI JSON filter resource.
     *
     * @param blocks The identifiers of the blocks to change the GUI textures of
     * @param entities The identifiers of the entities to change the GUI textures of
     * @param items The identifiers of the items to change the GUI textures of
     * @param inventory Whether to change the GUI textures of the inventory screen (creative, survival, or modded)
     * @param unknown Whether to change the GUI textures of inventory screens not initiated by player action (including
     *   screens opened by the server)
     * @param textureChangers A map containing the original textures and texture changers
     * @param spriteChangers A map containing the original sprites and sprite changers
     * @param textStyleChangers A list containing the text style changers
     * @param loadFilter Raw representation of a filter determining if the resource should be loaded
     * @param filter Raw representation of a filter filtering an interaction NBT
     */
    data class V2(
        val blocks: Set<Identifier>,
        val entities: Set<Identifier>,
        val items: Set<Identifier>,
        val inventory: Boolean,
        val unknown: Boolean,
        val textureChangers: Map<Identifier, JsonTextureChanger>,
        val spriteChangers: Map<Identifier, JsonTextureChanger>,
        val textStyleChangers: List<TextStyleChanger>,
        val loadFilter: INbtFilter,
        val filter: INbtFilter,
    ) : JsonFilterResource() {
        override val format: Int
            get() = 2

        companion object {
            /**
             * Key of [V2.blocks] in a JSON object.
             *
             * @see V2.blocks
             */
            const val BLOCKS_KEY = "blocks"

            /**
             * Key of [V2.entities] in a JSON object.
             *
             * @see V2.entities
             */
            const val ENTITIES_KEY = "entities"

            /**
             * Key of [V2.items] in a JSON object.
             *
             * @see V2.items
             */
            const val ITEMS_KEY = "items"

            /**
             * Key of [V2.inventory] in a JSON object.
             *
             * @see V2.inventory
             */
            const val INVENTORY_KEY = "inventory"

            /**
             * Key of [V2.unknown] in a JSON object.
             *
             * @see V2.unknown
             */
            const val UNKNOWN_KEY = "unknown"

            /**
             * Key of [V2.textureChangers] in a JSON object.
             *
             * @see V2.textureChangers
             */
            const val TEXTURE_CHANGERS_KEY = "change_textures"

            /**
             * Key of [V2.spriteChangers] in a JSON object.
             *
             * @see V2.spriteChangers
             */
            const val SPRITE_CHANGERS_KEY = "change_sprites"

            /**
             * Key of [V2.textStyleChangers] in a JSON object.
             *
             * @see V2.textStyleChangers
             */
            const val TEXT_STYLE_CHANGERS_KEY = "change_text_styles"

            /**
             * Key of [V2.loadFilter] in a JSON object.
             *
             * @see V2.loadFilter
             */
            const val LOAD_FILTER_KEY = "load_if"

            /**
             * Key of [V2.filter] in a JSON object.
             *
             * @see V2.filter
             */
            const val FILTER_KEY = "match"

            /**
             * A map codec for [JsonFilterResource.V2].
             */
            @JvmField
            val MAP_CODEC: MapCodec<V2> = RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    inventoryIdCodec().optionalField(BLOCKS_KEY, V2::blocks, emptySet()),
                    inventoryIdCodec().optionalField(ENTITIES_KEY, V2::entities, emptySet()),
                    inventoryIdCodec().optionalField(ITEMS_KEY, V2::items, emptySet()),
                    Codec.BOOL.optionalField(INVENTORY_KEY, V2::inventory, false),
                    Codec.BOOL.optionalField(UNKNOWN_KEY, V2::unknown, false),
                    Codec.unboundedMap(Identifier.CODEC, JsonTextureChanger.CODEC)
                        .optionalField(TEXTURE_CHANGERS_KEY, V2::textureChangers, emptyMap()),
                    Codec.unboundedMap(Identifier.CODEC, JsonTextureChanger.CODEC)
                        .optionalField(SPRITE_CHANGERS_KEY, V2::spriteChangers, emptyMap()),
                    TextStyleChanger.CODEC.listOf()
                        .optionalField(TEXT_STYLE_CHANGERS_KEY, V2::textStyleChangers, emptyList()),
                    INbtFilter.codec.optionalField(LOAD_FILTER_KEY, V2::loadFilter, ConditionalFilter.ALWAYS),
                    INbtFilter.codec.optionalField(FILTER_KEY, V2::filter, ConditionalFilter.ALWAYS),
                ).apply(instance, ::V2)
            }
        }
    }

    /**
     * Represents a future version of OptiGUI JSON filter resource.
     */
    data class Future(override val format: Int) : JsonFilterResource() {
        companion object {
            /**
             * A map codec for [JsonFilterResource.Future].
             */
            @JvmField
            val MAP_CODEC: MapCodec<Future> = RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    Codecs.POSITIVE_INT.optionalField(FORMAT_KEY, Future::format, 1)
                ).apply(instance, ::Future)
            }
        }
    }

    companion object {
        /**
         * Key of [JsonFilterResource.format] in a JSON object.
         *
         * @see JsonFilterResource.format
         */
        const val FORMAT_KEY = "format"

        /**
         * The newest supported JSON filter resource format.
         */
        const val NEWEST_FORMAT = 2

        /**
         * A codec for [JsonFilterResource].
         */
        @JvmField
        val CODEC: Codec<JsonFilterResource> = Codec.withAlternative(
            Codecs.POSITIVE_INT.dispatch(FORMAT_KEY, JsonFilterResource::format, ::getMapCodecForFormat),
            V1.MAP_CODEC.codec() // Codec.dispatch does not allow default typeKey if it is missing
        )

        private fun getMapCodecForFormat(format: Int) = when (format) {
            1 -> V1.MAP_CODEC
            2 -> V2.MAP_CODEC
            else -> Future.MAP_CODEC
        }

        private fun inventoryIdCodec() =
            Codec.withAlternative(Identifier.CODEC.listOf().toSet(), Identifier.CODEC, ::setOf)
    }
}
