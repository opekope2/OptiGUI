package opekope2.optigui.internal.inspector

import opekope2.optigui.internal.I18n

internal enum class DescriptionState(translation: I18n) {
    DEFAULT(I18n.OPTIGUI_INSPECTOR_DESCRIPTION),
    CLICKED(I18n.OPTIGUI_INSPECTOR_DESCRIPTION_CLICKED),
    DETAILED(I18n.OPTIGUI_INSPECTOR_DESCRIPTION_DEBUG),
    PROCESSING(I18n.OPTIGUI_INSPECTOR_DESCRIPTION_DEBUG_CLICKED),
    SUCCESS(I18n.OPTIGUI_INSPECTOR_DESCRIPTION_DEBUG_CLICKED_SUCCESS),
    ERROR(I18n.OPTIGUI_INSPECTOR_DESCRIPTION_DEBUG_CLICKED_ERROR);

    val isDetailed: Boolean
        get() = this != DEFAULT && this != CLICKED

    val description = translation.getText()

    companion object {
        fun of(detailed: Boolean, debuggerProcessing: Boolean) = when {
            !detailed -> DEFAULT
            debuggerProcessing -> PROCESSING
            else -> DETAILED
        }

        fun ofResult(success: Boolean) = if (success) SUCCESS else ERROR
    }
}
