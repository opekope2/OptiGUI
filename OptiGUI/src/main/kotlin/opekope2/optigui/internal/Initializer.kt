package opekope2.optigui.internal

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.gui.screen.ingame.BookEditScreen
import net.minecraft.client.gui.screen.ingame.BookScreen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import opekope2.optigui.registry.FilterLoaderRegistry
import opekope2.optigui.registry.RetexturableScreenRegistry
import opekope2.optigui.resource.OptiFineFilterLoader
import opekope2.optigui.resource.OptiGuiFilterLoader
import org.lwjgl.glfw.GLFW

@JvmField
internal val INSPECTOR_KEY_BINDING: KeyBinding = KeyBindingHelper.registerKeyBinding(
    KeyBinding(
        "key.optigui.inspect",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_F12,
        "key.categories.optigui"
    )
)

@Suppress("unused")
internal fun initialize() {
    RetexturableScreenRegistry.register(HandledScreen::class.java)
    RetexturableScreenRegistry.register(BookScreen::class.java)
    RetexturableScreenRegistry.register(BookEditScreen::class.java)
    RetexturableScreenRegistry.register(HangingSignEditScreen::class.java)

    FilterLoaderRegistry.register(OptiGuiFilterLoader())
    FilterLoaderRegistry.register(OptiFineFilterLoader())
}
