package opekope2.optigui.internal

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
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

internal fun initialize() {
}
