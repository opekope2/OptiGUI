package opekope2.optigui.internal.inspector

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.screen.ScreenTexts
import net.minecraft.util.Formatting
import opekope2.optigui.config.IConfig
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.internal.I18n
import opekope2.optigui.internal.IOptiGuiPlatform

@Environment(EnvType.CLIENT)
internal enum class InspectorTooltipFactory(private val i18n: I18n, private val formatting: Formatting) {
    ALPHA(I18n.OPTIGUI_INSPECTOR_TITLE_ALPHA, Formatting.RED),
    BETA(I18n.OPTIGUI_INSPECTOR_TITLE_BETA, Formatting.GOLD),
    STABLE(I18n.OPTIGUI_INSPECTOR_TITLE, Formatting.GREEN);

    private fun getTipText(customTextures: Boolean) =
        if (customTextures && IConfig.get().verboseInspector && InteractionManager.textureChangerFilter != null)
            I18n.OPTIGUI_INSPECTOR_TIP_VERBOSE.getText(InteractionManager.textureChangerFilter!!.resourceId)
                .formatted(Formatting.DARK_GRAY)
        else TIP_TEXT

    private fun getTooltipText(customTextures: Boolean, clickedDescription: Boolean) = ScreenTexts.joinLines(
        i18n.getText(if (customTextures) CUSTOM_TEXTURES_TEXT else ORIGINAL_TEXTURES_TEXT).formatted(formatting),
        if (clickedDescription) CLICKED_DESCRIPTION_TEXT else DESCRIPTION_TEXT,
        getTipText(customTextures)
    )

    fun createTooltip(customTextures: Boolean, clickedDescription: Boolean): Tooltip =
        Tooltip.of(getTooltipText(customTextures, clickedDescription))

    companion object {
        private val DESCRIPTION_TEXT = I18n.OPTIGUI_INSPECTOR_DESCRIPTION.getText()
        private val CLICKED_DESCRIPTION_TEXT = I18n.OPTIGUI_INSPECTOR_DESCRIPTION_CLICKED.getText()
        private val TIP_TEXT = I18n.OPTIGUI_INSPECTOR_TIP.getText().formatted(Formatting.DARK_GRAY)
        private val CUSTOM_TEXTURES_TEXT =
            I18n.OPTIGUI_INSPECTOR_TITLE_CUSTOM_TEXTURES.getText().formatted(Formatting.ITALIC)
        private val ORIGINAL_TEXTURES_TEXT = I18n.OPTIGUI_INSPECTOR_TITLE_ORIGINAL_TEXTURES.getText()

        @JvmField
        val CURRENT = when {
            "alpha" in IOptiGuiPlatform.version -> ALPHA
            "beta" in IOptiGuiPlatform.version -> BETA
            else -> STABLE
        }
    }
}
