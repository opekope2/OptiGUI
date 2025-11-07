package opekope2.optigui.config

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.JsonOps
import me.shedaniel.autoconfig.AutoConfig
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtOps
import net.minecraft.text.Style
import opekope2.optigui.filter.text_style_changer.TextStyleChanger
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.internal.I18n
import opekope2.optigui.internal.TextStyler
import opekope2.optigui.internal.config.Config
import opekope2.optigui.internal.inspector.JsonInspectorOps
import opekope2.optigui.nbt_provider.ILoadTimeNbtProvider

/**
 * OptiGUI Configuration
 */
interface IConfig {
    /**
     * Shows a button on every supported GUI screen to inspect the ongoing interaction.
     */
    var enableInspector: Boolean

    /**
     * Includes NBT data in the generated JSON filter resources.
     */
    var dumpNbt: InspectorNbtDumpOption

    /**
     * Attacking blocks, entities, or in the air also starts an interaction.
     */
    var interactWithAttackKey: Boolean

    /**
     * Keep [IInteraction.IFactory] after an interaction ends.
     */
    var keepInteractionFactory: Boolean

    /**
     * If there are problems loading resources, shows a screen with the details.
     */
    var showResourceLoadingErrors: ResourceLoadingErrorFilter

    /**
     * Saves the configuration to the disk.
     */
    fun save()

    /**
     * Resets the configuration to the defaults.
     */
    fun reset()

    /**
     * Options specifying what NBT to include the generated JSON filter resource.
     */
    enum class InspectorNbtDumpOption(private val ops: JsonInspectorOps, private val translation: I18n) {
        /**
         * No NBT is dumped.
         */
        DISABLED(JsonInspectorOps.WITHOUT_TYPE, I18n.OPTIGUI_ENUM_INSPECTORNBTDUMPOPTION_DISABLED) {
            private val disabledText = JsonPrimitive(I18n.OPTIGUI_INSPECTOR_NBT_DUMPING_DISABLED.getTranslation())

            override fun getLastRenderedTexts() = disabledText
            override fun getLoadTimeNbt() = disabledText
            override fun getInteractionNbt(interaction: IInteraction) = disabledText
        },

        /**
         * Only NBT values are dumped, not types.
         */
        VALUES_ONLY(JsonInspectorOps.WITHOUT_TYPE, I18n.OPTIGUI_ENUM_INSPECTORNBTDUMPOPTION_VALUES_ONLY),

        /**
         * Both NBT values and types are dumped.
         */
        VALUES_AND_TYPES(JsonInspectorOps.WITH_TYPE, I18n.OPTIGUI_ENUM_INSPECTORNBTDUMPOPTION_VALUES_AND_TYPES);

        open fun getLastRenderedTexts(): JsonElement = JsonArray().also { json ->
            InteractionManager.renderedStrings.forEach { source, text ->
                val textJson = TextStyler.stringWithSourceCodec.encodeStart(ops, Pair(text, source))
                val textStyleChangerJson = textJson.map { textStyleChanger(it, JsonObject()) }
                textStyleChangerJson.ifSuccess(json::add)
            }
            InteractionManager.renderedTexts.forEach { source, text ->
                val textJson = TextStyler.textWithSourceCodec.encodeStart(ops, Pair(text, source))
                val styleJson = Style.Codecs.CODEC.encodeStart(JsonOps.INSTANCE, text.style)
                val textStyleChangerJson = textJson.apply2stable(::textStyleChanger, styleJson)
                textStyleChangerJson.ifSuccess(json::add)
            }
        }

        private fun textStyleChanger(text: JsonElement, style: JsonElement) = JsonObject().also { json ->
            json.add(TextStyleChanger.FILTER_KEY, text)
            json.add(TextStyleChanger.SET_STYLE_KEY, style)
            json.addProperty(TextStyleChanger.OVERRIDE_KEY, false)
        }

        open fun getLoadTimeNbt(): JsonElement = JsonObject().apply {
            for ((key, supplier) in ILoadTimeNbtProvider.Registry) add(key, supplier.get().toJson())
        }

        open fun getInteractionNbt(interaction: IInteraction): JsonElement = interaction.createNbt().toJson()

        private fun NbtElement.toJson() = NbtOps.INSTANCE.convertTo(ops, this)

        override fun toString() = translation.getTranslation()
    }

    /**
     * Resource loading problem filter.
     */
    enum class ResourceLoadingErrorFilter(private val translation: I18n) {
        /**
         * Filter for no problems
         */
        NOTHING(I18n.OPTIGUI_ENUM_RESOURCELOADINGERRORFILTER_NOTHING),

        /**
         * Filter for errors only
         */
        ERRORS_ONLY(I18n.OPTIGUI_ENUM_RESOURCELOADINGERRORFILTER_ERRORS_ONLY),

        /**
         * Filter for errors and warnings
         */
        ERRORS_AND_WARNINGS(I18n.OPTIGUI_ENUM_RESOURCELOADINGERRORFILTER_ERRORS_AND_WARNINGS);

        override fun toString() = translation.getTranslation()
    }

    companion object {
        /**
         * Gets the current OptiGUI configuration.
         */
        @JvmStatic
        fun get(): IConfig = AutoConfig.getConfigHolder(Config::class.java).config
    }
}
