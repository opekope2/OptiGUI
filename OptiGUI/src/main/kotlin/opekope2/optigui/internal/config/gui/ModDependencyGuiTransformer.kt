package opekope2.optigui.internal.config.gui

import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess
import me.shedaniel.autoconfig.gui.registry.api.GuiTransformer
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry
import me.shedaniel.clothconfig2.api.Requirement
import opekope2.optigui.internal.IOptiGuiPlatform
import opekope2.optigui.internal.config.annotation.RequiresMod
import java.lang.reflect.Field

internal object ModDependencyGuiTransformer : GuiTransformer {
    override fun transform(
        guis: List<AbstractConfigListEntry<*>>,
        i18n: String,
        field: Field,
        config: Any,
        defaults: Any,
        guiProvider: GuiRegistryAccess
    ): List<AbstractConfigListEntry<*>> {
        for (gui in guis) {
            val annotation = field.getAnnotation(RequiresMod::class.java)!!
            val modId = annotation.modId
            var requirement = Requirement.isTrue { IOptiGuiPlatform.isModInstalled(modId) }
            if (annotation.inverse) requirement = Requirement.not(requirement)
            gui.requirement = requirement
        }
        return guis
    }
}
