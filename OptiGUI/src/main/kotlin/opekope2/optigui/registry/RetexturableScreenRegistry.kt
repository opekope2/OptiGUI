package opekope2.optigui.registry

import net.minecraft.client.gui.screen.Screen

/**
 * Registry holding screens, which can be retextured by OptiGUI.
 */
object RetexturableScreenRegistry {
    private val retexturableScreens = mutableSetOf<Class<out Screen>>()

    /**
     * Registers a screen and all of its subclasses as retexturable.
     *
     * @param screenClass The class of the screen
     */
    @JvmStatic
    fun register(screenClass: Class<out Screen>) {
        retexturableScreens += screenClass
    }

    /**
     * Checks if a given screen is registered as retexturable.
     *
     * @param screen The screen instance to check
     */
    @JvmStatic
    operator fun contains(screen: Screen): Boolean = retexturableScreens.any { it.isAssignableFrom(screen.javaClass) }
}
