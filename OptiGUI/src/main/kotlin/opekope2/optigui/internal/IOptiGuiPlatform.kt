package opekope2.optigui.internal

import me.shedaniel.autoconfig.ConfigData
import net.minecraft.resource.SynchronousResourceReloader
import opekope2.optigui.internal.config.Config
import opekope2.optigui.internal.initializer.ClientInitializer
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
interface IOptiGuiPlatform {
    val version: String

    fun isModInstalled(modId: String): Boolean

    @ApiStatus.Internal
    companion object Instance : IOptiGuiPlatform {
        private lateinit var instance: IOptiGuiPlatform

        override val version: String
            get() = instance.version

        override fun isModInstalled(modId: String) = instance.isModInstalled(modId)


        val configClass: Class<out ConfigData>
            get() = Config::class.java

        var renderingScreen: Boolean
            get() = TextureChanger.renderingScreen
            set(value) {
                TextureChanger.renderingScreen = value
            }

        val textureChanger: SynchronousResourceReloader
            get() = TextureChanger

        fun initialize(platform: IOptiGuiPlatform) {
            check(!::instance.isInitialized) { "Tried to initialize OptiGUI platform twice" }

            instance = platform
            ClientInitializer
        }
    }
}
