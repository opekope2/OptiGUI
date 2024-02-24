package opekope2.optigui.internal

import net.minecraft.client.gui.screen.Screen
import opekope2.optigui.internal.service.RetexturableScreensRegistryService

internal object RetexturableScreensRegistry : RetexturableScreensRegistryService {
    private val retexturableScreens = mutableSetOf<Class<out Screen>>()

    override fun addRetexturableScreen(screenClass: Class<out Screen>) {
        retexturableScreens += screenClass
    }

    override fun isScreenRetexturable(screen: Screen): Boolean = retexturableScreens.any { it.isAssignableFrom(screen.javaClass) }
}
