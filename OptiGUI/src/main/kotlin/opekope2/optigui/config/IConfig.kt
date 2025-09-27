package opekope2.optigui.config

import me.shedaniel.autoconfig.AutoConfig
import opekope2.optigui.internal.config.Config

/**
 * OptiGUI Configuration
 */
interface IConfig {
    /**
     * Shows a button on every supported GUI screen to inspect the ongoing interaction.
     */
    var enableInspector: Boolean

    /**
     * Shows extra information on the inspector button's tooltip.
     */
    var verboseInspector: Boolean

    /**
     * Includes NBT data in the generated JSON resource.
     */
    var dumpNbt: Boolean

    /**
     * Saves the configuration to the disk.
     */
    fun save()

    /**
     * Resets the configuration to the defaults.
     */
    fun reset()

    companion object {
        /**
         * Gets the current OptiGUI configuration.
         */
        @JvmStatic
        fun get(): IConfig = AutoConfig.getConfigHolder(Config::class.java).config
    }
}
