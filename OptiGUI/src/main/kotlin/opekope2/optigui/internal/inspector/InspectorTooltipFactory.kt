package opekope2.optigui.internal.inspector

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import opekope2.optigui.config.IConfig
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.internal.IOptiGuiPlatform

@Environment(EnvType.CLIENT)
internal enum class InspectorTooltipFactory(private val translationKey: String, private val formatting: Formatting) {
    ALPHA("optigui.inspector.title.alpha", Formatting.RED),
    BETA("optigui.inspector.title.beta", Formatting.GOLD),
    STABLE("optigui.inspector.title", Formatting.GREEN);

    private fun getTipText(customTextures: Boolean) =
        if (customTextures && IConfig.get().verboseInspector && InteractionManager.textureChangerFilter != null)
            Text.translatable("optigui.inspector.tip.verbose", InteractionManager.textureChangerFilter!!.resourceId)
                .formatted(Formatting.DARK_GRAY)
        else TIP_TEXT

    private fun getTooltipText(customTextures: Boolean, clickedDescription: Boolean) = ScreenTexts.joinLines(
        Text.translatable(
            translationKey,
            if (customTextures) CUSTOM_TEXTURES_TEXT else ORIGINAL_TEXTURES_TEXT
        ).formatted(formatting),
        if (clickedDescription) CLICKED_DESCRIPTION_TEXT else DESCRIPTION_TEXT,
        getTipText(customTextures)
    )

    fun createTooltip(customTextures: Boolean, clickedDescription: Boolean): Tooltip =
        Tooltip.of(getTooltipText(customTextures, clickedDescription))

    companion object {
        private val DESCRIPTION_TEXT = Text.translatable("optigui.inspector.description")
        private val CLICKED_DESCRIPTION_TEXT = Text.translatable("optigui.inspector.description.clicked")
        private val TIP_TEXT = Text.translatable("optigui.inspector.tip").formatted(Formatting.DARK_GRAY)
        private val CUSTOM_TEXTURES_TEXT = Text.translatable("optigui.inspector.title.custom_textures")
        private val ORIGINAL_TEXTURES_TEXT = Text.translatable("optigui.inspector.title.original_textures")

        val CURRENT = when {
            "alpha" in IOptiGuiPlatform.version -> ALPHA
            "beta" in IOptiGuiPlatform.version -> BETA
            else -> STABLE
        }
    }
}
