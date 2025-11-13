package opekope2.optigui.internal.config

import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry
import net.minecraft.client.Minecraft
import opekope2.optigui.config.IConfig
import opekope2.optigui.gui.screen.ResourceLoadingErrorScreen
import opekope2.optigui.internal.config.gui.ButtonListEntry
import opekope2.optigui.util.MOD_ID

@Config(name = MOD_ID)
internal class Config : IConfig, ConfigData {
    @ConfigEntry.Category("inspector")
    @ConfigEntry.Gui.Tooltip
    override var enableInspector = true

    @ConfigEntry.Category("inspector")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.Tooltip
    override var dumpNbt = IConfig.InspectorNbtDumpOption.DISABLED

    @ConfigEntry.Category("interaction")
    @ConfigEntry.Gui.Tooltip(count = 2)
    override var interactWithAttackKey: Boolean = false

    @ConfigEntry.Category("interaction")
    @ConfigEntry.Gui.Tooltip(count = 2)
    override var keepInteractionFactory: Boolean = false

    @ConfigEntry.Category("resourceLoading")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.Tooltip
    override var showResourceLoadingErrors = IConfig.ResourceLoadingErrorFilter.ERRORS_ONLY

    @ConfigEntry.Category("utils")
    @ConfigEntry.Gui.Tooltip
    @Transient
    @Suppress("unused")
    val errorsAndWarnings = ButtonListEntry.IAction {
        val client = Minecraft.getInstance()
        val screen = client.screen
        client.setScreen(ResourceLoadingErrorScreen.create(ResourceLoadingErrorScreen.setScreen(screen)))
    }

    override fun save() {
        AutoConfig.getConfigHolder(javaClass).save()
    }

    override fun reset() {
        AutoConfig.getConfigHolder(javaClass).resetToDefault()
    }
}
