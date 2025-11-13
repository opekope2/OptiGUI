package opekope2.optigui.internal.config.gui

import me.shedaniel.autoconfig.annotation.ConfigEntry
import me.shedaniel.autoconfig.gui.registry.api.GuiProvider
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry
import net.minecraft.network.chat.Component
import java.lang.reflect.Field

internal object ButtonEntryGuiProvider : GuiProvider {
    override fun get(
        i18n: String,
        field: Field,
        config: Any,
        defaults: Any,
        registry: GuiRegistryAccess
    ): List<AbstractConfigListEntry<*>> {
        field.isAccessible = true
        return listOf(
            ButtonListEntry(
                Component.translatable(i18n),
                Component.translatable("$i18n.button"),
                Component.translatable("$i18n.@Tooltip")
                    .takeIf { field.getAnnotation(ConfigEntry.Gui.Tooltip::class.java) != null },
                field.get(config) as ButtonListEntry.IAction
            )
        )
    }
}
