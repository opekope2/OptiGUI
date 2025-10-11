package opekope2.optigui.internal.inspector

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.internal.I18n
import opekope2.optigui.internal.IOptiGuiPlatform

@Environment(EnvType.CLIENT)
internal enum class InspectorTooltipFactory(private val translation: I18n, private val formatting: Formatting) {
    ALPHA(I18n.OPTIGUI_INSPECTOR_TITLE_ALPHA, Formatting.RED),
    BETA(I18n.OPTIGUI_INSPECTOR_TITLE_BETA, Formatting.GOLD),
    STABLE(I18n.OPTIGUI_INSPECTOR_TITLE, Formatting.GREEN);

    private fun getTooltipText(customTextures: Boolean, clickedDescription: Boolean) = ScreenTexts.joinLines(
        translation.getText(if (customTextures) CUSTOM_TEXTURES_TEXT else ORIGINAL_TEXTURES_TEXT).formatted(formatting),
        if (clickedDescription) CLICKED_DESCRIPTION_TEXT else DESCRIPTION_TEXT,
        MORE_INFO_TEXT
    )

    private fun getDetailedTooltipText(customTextures: Boolean, clickedDescription: Boolean) = ScreenTexts.joinLines(
        buildList {
            this += translation.getText(
                if (customTextures) CUSTOM_TEXTURES_TEXT
                else ORIGINAL_TEXTURES_TEXT
            ).formatted(formatting)
            this += if (clickedDescription) CLICKED_DESCRIPTION_TEXT else DESCRIPTION_TEXT
            // TODO add debugger
            // this += DEBUG_DESCRIPTION_TEXT
            this += HIDE_DESCRIPTION_TEXT
            if (customTextures && InteractionManager.textureChangerFilter != null)
                this += I18n.OPTIGUI_INSPECTOR_DESCRIPTION_CUSTOM_TEXTURES.getText(InteractionManager.textureChangerFilter!!.resourceId)
        }
    )

    fun createTooltip(customTextures: Boolean, clickedDescription: Boolean, moreInfo: Boolean): Tooltip = Tooltip.of(
        if (moreInfo) getDetailedTooltipText(customTextures, clickedDescription)
        else getTooltipText(customTextures, clickedDescription)
    )

    companion object {
        private val DESCRIPTION_TEXT = I18n.OPTIGUI_INSPECTOR_DESCRIPTION.getText()
        private val CLICKED_DESCRIPTION_TEXT = I18n.OPTIGUI_INSPECTOR_DESCRIPTION_CLICKED.getText()
        private val MORE_INFO_TEXT = I18n.OPTIGUI_INSPECTOR_KEY_MORE_INFO.getText(Text.keybind("key.keyboard.left.alt"))
            .formatted(Formatting.DARK_GRAY)
        private val DEBUG_DESCRIPTION_TEXT =
            I18n.OPTIGUI_INSPECTOR_DESCRIPTION_DEBUG.getText(Text.keybind("key.keyboard.left.shift"))
        private val HIDE_DESCRIPTION_TEXT = I18n.OPTIGUI_INSPECTOR_DESCRIPTION_HIDE.getText()
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
