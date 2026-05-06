package opekope2.optigui.internal.inspector

import opekope2.optigui.internal.I18n

internal enum class DescriptionState(translation: I18n) {
    DEFAULT(I18n.OPTIGUI_INSPECTOR_DESCRIPTION),
    CLICKED(I18n.OPTIGUI_INSPECTOR_DESCRIPTION_CLICKED),
    DETAILED(I18n.OPTIGUI_INSPECTOR_DESCRIPTION_DEBUG);

    val isDetailed: Boolean
        get() = this == DETAILED

    val description = translation.getText()

    companion object {
        fun get(detailed: Boolean) = if (detailed) DETAILED else DEFAULT
    }
}
