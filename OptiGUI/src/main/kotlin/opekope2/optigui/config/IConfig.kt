package opekope2.optigui.config

import me.shedaniel.autoconfig.AutoConfig
import opekope2.optigui.internal.config.Config

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
    var dumpNbt: InspectorNbtDumpOptions

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
    enum class InspectorNbtDumpOptions(private val ops: JsonInspectorOps, private val translation: I18n) {
        /**
         * No NBT is dumped.
         */
        DISABLED(
            JsonInspectorOps.WITHOUT_TYPE,
            I18n.OPTIGUI_ENUM_INSPECTORNBTDUMPER_DISABLED
        ) {
            private val disabledText = JsonPrimitive(I18n.OPTIGUI_INSPECTOR_NBT_DUMPING_DISABLED.getTranslation())

            override fun getLastRenderedTexts() = disabledText
            override fun getLoadTimeNbt() = disabledText
            override fun getInteractionNbt(interaction: IInteraction) = disabledText
        },

        /**
         * Only NBT values are dumped, not types.
         */
        ENABLED_VALUES_ONLY(
            JsonInspectorOps.WITHOUT_TYPE,
            I18n.OPTIGUI_ENUM_INSPECTORNBTDUMPER_ENABLED_VALUES_ONLY
        ),

        /**
         * Both NBT values and types are dumped.
         */
        ENABLED_VALUES_AND_TYPES(
            JsonInspectorOps.WITH_TYPE,
            I18n.OPTIGUI_ENUM_INSPECTORNBTDUMPER_ENABLED_VALUES_AND_TYPES
        );

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

    companion object {
        /**
         * Gets the current OptiGUI configuration.
         */
        @JvmStatic
        fun get(): IConfig = AutoConfig.getConfigHolder(Config::class.java).config
    }
}
