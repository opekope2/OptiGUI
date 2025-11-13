package opekope2.optigui.internal.inspector

import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.internal.I18n
import opekope2.optigui.internal.IOptiGuiPlatform

internal enum class InspectorTooltipStyle(private val translation: I18n, private val titleFormatting: Formatting) {
    ALPHA(I18n.OPTIGUI_INSPECTOR_TITLE_ALPHA, Formatting.RED),
    BETA(I18n.OPTIGUI_INSPECTOR_TITLE_BETA, Formatting.GOLD),
    STABLE(I18n.OPTIGUI_INSPECTOR_TITLE, Formatting.GREEN);

    private fun getTitleText(customTextures: Boolean): MutableText? {
        val text = if (customTextures) CUSTOM_TEXTURES_TEXT else ORIGINAL_TEXTURES_TEXT
        return translation.getText(text).formatted(titleFormatting)
    }

    private fun getTooltipText(customTextures: Boolean, descriptionState: DescriptionState) =
        ScreenTexts.joinLines(getTitleText(customTextures), descriptionState.description, MORE_INFO_TEXT)

    private fun getDetailedTooltipText(customTextures: Boolean, descriptionState: DescriptionState) =
        ScreenTexts.joinLines(
            buildList {
                add(getTitleText(customTextures))
                add(descriptionState.description)
                if (customTextures && InteractionManager.textureChangerFilter != null) add(getCustomResourceIdText())
                add(HIDE_DESCRIPTION_TEXT)
            }
        )

    private fun getCustomResourceIdText() =
        Text.literal(InteractionManager.textureChangerFilter!!.resourceId.toString())
            .formatted(Formatting.GRAY, Formatting.ITALIC)

    fun createTooltip(customTextures: Boolean, debuggerState: DescriptionState): Tooltip = Tooltip.of(
        if (debuggerState.isDetailed) getDetailedTooltipText(customTextures, debuggerState)
        else getTooltipText(customTextures, debuggerState)
    )

    companion object {
        private val MORE_INFO_TEXT = I18n.OPTIGUI_INSPECTOR_KEY_MORE_INFO.getText(Text.keybind("key.keyboard.left.alt"))
            .formatted(Formatting.DARK_GRAY)
        private val HIDE_DESCRIPTION_TEXT =
            I18n.OPTIGUI_INSPECTOR_DESCRIPTION_HIDE.getText().formatted(Formatting.DARK_GRAY)
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
