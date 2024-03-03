package opekope2.optigui.internal

import net.minecraft.client.gui.screen.ingame.BookEditScreen
import net.minecraft.client.gui.screen.ingame.BookScreen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen
import opekope2.optigui.registry.FilterLoaderRegistry
import opekope2.optigui.registry.RetexturableScreenRegistry
import opekope2.optigui.resource.OptiFineFilterLoader
import opekope2.optigui.resource.OptiGuiFilterLoader

@Suppress("unused")
fun initialize() {
    RetexturableScreenRegistry.register(HandledScreen::class.java)
    RetexturableScreenRegistry.register(BookScreen::class.java)
    RetexturableScreenRegistry.register(BookEditScreen::class.java)
    RetexturableScreenRegistry.register(HangingSignEditScreen::class.java)

    FilterLoaderRegistry.register(OptiGuiFilterLoader())
    FilterLoaderRegistry.register(OptiFineFilterLoader())
}
