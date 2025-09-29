package opekope2.optigui.internal.config

import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry
import opekope2.optigui.config.IConfig
import opekope2.optigui.util.MOD_ID

@Config(name = MOD_ID)
internal class Config : IConfig, ConfigData {
    @ConfigEntry.Category("inspector")
    @ConfigEntry.Gui.Tooltip
    override var enableInspector = true

    @ConfigEntry.Category("inspector")
    @ConfigEntry.Gui.Tooltip
    override var verboseInspector = false

    @ConfigEntry.Category("inspector")
    @ConfigEntry.Gui.Tooltip
    override var dumpNbt: Boolean = false

    override fun save() {
        AutoConfig.getConfigHolder(javaClass).save()
    }

    override fun reset() {
        AutoConfig.getConfigHolder(javaClass).resetToDefault()
    }
}
