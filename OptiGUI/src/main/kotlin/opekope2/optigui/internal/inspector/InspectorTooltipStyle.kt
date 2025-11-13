package opekope2.optigui.internal.inspector

import net.minecraft.ChatFormatting
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.internal.I18n
import opekope2.optigui.internal.IOptiGuiPlatform

internal enum class InspectorTooltipStyle(private val translation: I18n, private val titleFormatting: ChatFormatting) {
    ALPHA(I18n.OPTIGUI_INSPECTOR_TITLE_ALPHA, ChatFormatting.RED),
    BETA(I18n.OPTIGUI_INSPECTOR_TITLE_BETA, ChatFormatting.GOLD),
    STABLE(I18n.OPTIGUI_INSPECTOR_TITLE, ChatFormatting.GREEN);

    private fun getTitleText(customTextures: Boolean): MutableComponent {
        val text = if (customTextures) CUSTOM_TEXTURES_TEXT else ORIGINAL_TEXTURES_TEXT
        return translation.getText(text).withStyle(titleFormatting)
    }

    private fun getTooltipText(customTextures: Boolean, descriptionState: DescriptionState) =
        CommonComponents.joinLines(getTitleText(customTextures), descriptionState.description, MORE_INFO_TEXT)

    private fun getDetailedTooltipText(customTextures: Boolean, descriptionState: DescriptionState) =
        CommonComponents.joinLines(
            buildList {
                add(getTitleText(customTextures))
                add(descriptionState.description)
                if (customTextures && InteractionManager.textureChangerFilter != null) add(getCustomResourceIdText())
                add(HIDE_DESCRIPTION_TEXT)
            }
        )

    private fun getCustomResourceIdText() =
        Component.literal(InteractionManager.textureChangerFilter!!.resourceId.toString())
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)

    fun createTooltip(customTextures: Boolean, debuggerState: DescriptionState) = Tooltip.create(
        if (debuggerState.isDetailed) getDetailedTooltipText(customTextures, debuggerState)
        else getTooltipText(customTextures, debuggerState)
    )

    companion object {
        private val MORE_INFO_TEXT =
            I18n.OPTIGUI_INSPECTOR_KEY_MORE_INFO.getText(Component.keybind("key.keyboard.left.alt"))
                .withStyle(ChatFormatting.DARK_GRAY)
        private val HIDE_DESCRIPTION_TEXT =
            I18n.OPTIGUI_INSPECTOR_DESCRIPTION_HIDE.getText().withStyle(ChatFormatting.DARK_GRAY)
        private val CUSTOM_TEXTURES_TEXT =
            I18n.OPTIGUI_INSPECTOR_TITLE_CUSTOM_TEXTURES.getText().withStyle(ChatFormatting.ITALIC)
        private val ORIGINAL_TEXTURES_TEXT = I18n.OPTIGUI_INSPECTOR_TITLE_ORIGINAL_TEXTURES.getText()

        @JvmField
        val CURRENT = when {
            "alpha" in IOptiGuiPlatform.version -> ALPHA
            "beta" in IOptiGuiPlatform.version -> BETA
            else -> STABLE
        }
    }
}
