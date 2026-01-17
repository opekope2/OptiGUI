package opekope2.optigui.config

import com.mojang.datafixers.util.Pair
import dev.runefox.json.JsonArray
import dev.runefox.json.JsonNode
import dev.runefox.json.JsonObject
import dev.runefox.json.JsonString
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Style
import opekope2.optigui.filter.text_style_changer.TextStyleChanger
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.internal.I18n
import opekope2.optigui.internal.TextStyler
import opekope2.optigui.internal.inspector.JsonInspectorOps
import opekope2.optigui.nbt_provider.ILoadTimeNbtProvider
import opekope2.optigui.util.dfu.Json5Ops

/**
 * Options specifying what NBT to include the generated JSON filter resource.
 */
enum class InspectorNbtDumper(private val ops: JsonInspectorOps) {
    /**
     * Dumps no NBT.
     */
    NOTHING(JsonInspectorOps.WITHOUT_TYPE) {
        private val disabledText = JsonString(I18n.OPTIGUI_INSPECTOR_NBT_DUMPING_DISABLED.getTranslation())

        override fun getLastRenderedTexts() = disabledText
        override fun getLoadTimeNbt() = disabledText
        override fun getInteractionNbt(interaction: IInteraction) = disabledText
    },

    /**
     * Only dumps NBT values, not types.
     */
    VALUES_ONLY(JsonInspectorOps.WITHOUT_TYPE),

    /**
     * Dumps both NBT values and types.
     */
    VALUES_AND_TYPES(JsonInspectorOps.WITH_TYPE);

    /**
     * Gets the texts rendered on the screen as JSON to be included in a generated JSON resource.
     */
    open fun getLastRenderedTexts(): JsonNode = JsonArray { json ->
        TextStyler.renderedStrings.forEach { source, text ->
            val textJson = TextStyler.stringWithSourceCodec.encodeStart(ops, Pair(text, source))
            val textStyleChangerJson = textJson.map { textStyleChanger(it, JsonObject()) }
            textStyleChangerJson.ifSuccess(json::add)
        }
        TextStyler.renderedTexts.forEach { source, text ->
            val textJson = TextStyler.textWithSourceCodec.encodeStart(ops, Pair(text, source))
            val styleJson = Style.Serializer.CODEC.encodeStart(Json5Ops, text.style)
            val textStyleChangerJson = textJson.apply2stable(::textStyleChanger, styleJson)
            textStyleChangerJson.ifSuccess(json::add)
        }
    }

    private fun textStyleChanger(text: JsonNode, style: JsonNode) = JsonObject { json ->
        json[TextStyleChanger.FILTER_KEY] = text
        json[TextStyleChanger.SET_STYLE_KEY] = style
        json[TextStyleChanger.OVERRIDE_KEY] = false
    }

    /**
     * Gets the load-time NBT as JSON to be included in a generated JSON resource.
     */
    open fun getLoadTimeNbt(): JsonNode = JsonObject {
        for ((key, supplier) in ILoadTimeNbtProvider.Registry) it["$$key"] = supplier.get().toJson()
    }

    /**
     * Gets interaction NBT as JSON to be included in a generated JSON resource.
     *
     * @param interaction The interaction to dump the NBT of
     */
    open fun getInteractionNbt(interaction: IInteraction): JsonNode = interaction.createNbt().toJson()

    private fun Tag.toJson() = NbtOps.INSTANCE.convertTo(ops, this)
}
