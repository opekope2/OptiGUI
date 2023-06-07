package opekope2.optigui.internal.service

import net.minecraft.client.gui.screen.Screen

interface RetexturableScreensRegistryService {
    fun addRetexturableScreen(screenClass: Class<out Screen>)

    fun isScreenRetexturable(screen: Screen): Boolean
}
