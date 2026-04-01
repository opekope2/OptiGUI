package opekope2.optigui.internal

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.minecraft.client.gui.screens.inventory.BookEditScreen
import net.minecraft.client.gui.screens.inventory.BookViewScreen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen
import net.minecraft.client.KeyMapping
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.resources.Identifier
import opekope2.optigui.registry.FilterLoaderRegistry
import opekope2.optigui.registry.RetexturableScreenRegistry
import opekope2.optigui.resource.OptiFineFilterLoader
import opekope2.optigui.resource.OptiGuiFilterLoader
import opekope2.optigui.util.MOD_ID
import org.lwjgl.glfw.GLFW

@JvmField
internal val OPTIGUI_INSPECTOR_CATEGORY =
    KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "inspector"))

@JvmField
internal val INSPECTOR_KEY_BINDING: KeyMapping = KeyMappingHelper.registerKeyMapping(
    KeyMapping(
        "key.optigui.inspect",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_F12,
        OPTIGUI_INSPECTOR_CATEGORY
    )
)

internal fun initialize() {
    RetexturableScreenRegistry.register(AbstractContainerScreen::class.java)
    RetexturableScreenRegistry.register(BookViewScreen::class.java)
    RetexturableScreenRegistry.register(BookEditScreen::class.java)
    RetexturableScreenRegistry.register(HangingSignEditScreen::class.java)

    FilterLoaderRegistry.register(OptiGuiFilterLoader())
    FilterLoaderRegistry.register(OptiFineFilterLoader())
}
