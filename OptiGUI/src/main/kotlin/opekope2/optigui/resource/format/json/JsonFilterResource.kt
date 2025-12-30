package opekope2.optigui.resource.format.json

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import opekope2.optigui.filter.ConditionalFilter
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.filter.text_style_changer.TextStyleChanger
import opekope2.optigui.util.dfu.optionalField
import opekope2.optigui.util.dfu.toSet

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
data class JsonFilterResource(
    val blocks: Set<ResourceLocation>,
    val entities: Set<ResourceLocation>,
    val items: Set<ResourceLocation>,
    val inventory: Boolean,
    val unknown: Boolean,
    val textureChangers: Map<ResourceLocation, JsonTextureChanger>,
    val spriteChangers: Map<ResourceLocation, JsonTextureChanger>,
    val textStyleChangers: List<TextStyleChanger>,
    val loadFilter: INbtFilter,
    val filter: INbtFilter,
) {
    companion object {
        /**
         * Key of [blocks] in a JSON object.
         *
         * @see blocks
         */
        const val BLOCKS_KEY = "blocks"

        /**
         * Key of [entities] in a JSON object.
         *
         * @see entities
         */
        const val ENTITIES_KEY = "entities"

        /**
         * Key of [items] in a JSON object.
         *
         * @see items
         */
        const val ITEMS_KEY = "items"

        /**
         * Key of [inventory] in a JSON object.
         *
         * @see inventory
         */
        const val INVENTORY_KEY = "inventory"

        /**
         * Key of [unknown] in a JSON object.
         *
         * @see unknown
         */
        const val UNKNOWN_KEY = "unknown"

        /**
         * Key of [textureChangers] in a JSON object.
         *
         * @see textureChangers
         */
        const val TEXTURE_CHANGERS_KEY = "change_textures"

        /**
         * Key of [spriteChangers] in a JSON object.
         *
         * @see spriteChangers
         */
        const val SPRITE_CHANGERS_KEY = "change_sprites"

        /**
         * Key of [textStyleChangers] in a JSON object.
         *
         * @see textStyleChangers
         */
        const val TEXT_STYLE_CHANGERS_KEY = "change_text_styles"

        /**
         * Key of [loadFilter] in a JSON object.
         *
         * @see loadFilter
         */
        const val LOAD_FILTER_KEY = "load_if"

        /**
         * Key of [filter] in a JSON object.
         *
         * @see filter
         */
        const val FILTER_KEY = "match"

        /**
         * A map codec for [JsonFilterResource].
         */
        @JvmField
        val CODEC: Codec<JsonFilterResource> = RecordCodecBuilder.create { instance ->
            instance.group(
                inventoryIdCodec().optionalField(BLOCKS_KEY, JsonFilterResource::blocks, emptySet()),
                inventoryIdCodec().optionalField(ENTITIES_KEY, JsonFilterResource::entities, emptySet()),
                inventoryIdCodec().optionalField(ITEMS_KEY, JsonFilterResource::items, emptySet()),
                Codec.BOOL.optionalField(INVENTORY_KEY, JsonFilterResource::inventory, false),
                Codec.BOOL.optionalField(UNKNOWN_KEY, JsonFilterResource::unknown, false),
                Codec.unboundedMap(ResourceLocation.CODEC, JsonTextureChanger.CODEC)
                    .optionalField(TEXTURE_CHANGERS_KEY, JsonFilterResource::textureChangers, emptyMap()),
                Codec.unboundedMap(ResourceLocation.CODEC, JsonTextureChanger.CODEC)
                    .optionalField(SPRITE_CHANGERS_KEY, JsonFilterResource::spriteChangers, emptyMap()),
                TextStyleChanger.CODEC.listOf()
                    .optionalField(TEXT_STYLE_CHANGERS_KEY, JsonFilterResource::textStyleChangers, emptyList()),
                INbtFilter.CODEC.optionalField(
                    LOAD_FILTER_KEY,
                    JsonFilterResource::loadFilter,
                    ConditionalFilter.ALWAYS
                ),
                INbtFilter.CODEC.optionalField(FILTER_KEY, JsonFilterResource::filter, ConditionalFilter.ALWAYS),
            ).apply(instance, ::JsonFilterResource)
        }

        private fun inventoryIdCodec() =
            Codec.withAlternative(ResourceLocation.CODEC.listOf().toSet(), ResourceLocation.CODEC, ::setOf)
    }
}
