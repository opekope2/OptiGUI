package opekope2.optigui.internal

import opekope2.optigui.internal.initializer.ClientInitializer
import opekope2.optigui.internal.initializer.initialize
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
interface IOptiGuiPlatform {
    val version: String

    fun isModInstalled(modId: String): Boolean

    companion object Holder {
        private lateinit var instance: IOptiGuiPlatform

        @JvmStatic
        fun get() = instance

        @JvmStatic
        fun initialize(platform: IOptiGuiPlatform) {
            check(!::instance.isInitialized) { "Tried to initialize OptiGUI platform twice" }

            instance = platform
            ClientInitializer.initialize()
        }
    }
}
