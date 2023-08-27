package opekope2.optigui.internal

import net.minecraft.client.gui.screen.Screen
import opekope2.util.isSuperOf

private val retexturableScreens = mutableSetOf<Class<out Screen>>()

internal fun addRetexturableScreen(screenClass: Class<out Screen>) {
    retexturableScreens += screenClass
}

internal val Screen.isRetexturable: Boolean
    get() = retexturableScreens.any { it.isSuperOf(this) }
