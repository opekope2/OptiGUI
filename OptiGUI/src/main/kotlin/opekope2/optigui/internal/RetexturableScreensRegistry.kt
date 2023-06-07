package opekope2.optigui.internal

import net.minecraft.client.gui.screen.Screen
import opekope2.optigui.internal.service.RetexturableScreensRegistryService
import opekope2.util.isSuperOf

internal class RetexturableScreensRegistry : RetexturableScreensRegistryService {
    private val retexturableScreens = mutableSetOf<Class<out Screen>>()

    override fun addRetexturableScreen(screenClass: Class<out Screen>) {
        retexturableScreens += screenClass
    }

    override fun isScreenRetexturable(screen: Screen): Boolean = retexturableScreens.any { it.isSuperOf(screen) }
}
