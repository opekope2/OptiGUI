package opekope2.optigui.internal

import net.fabricmc.loader.api.Version
import net.minecraft.client.gui.screen.ingame.BookEditScreen
import net.minecraft.client.gui.screen.ingame.BookScreen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen
import opekope2.lilac.util.Util
import opekope2.optigui.api.IOptiGuiApi


@Suppress("unused")
fun initialize() {
    IOptiGuiApi.getImplementation().apply {
        addRetexturableScreen(HandledScreen::class.java)
        addRetexturableScreen(BookScreen::class.java)
        addRetexturableScreen(BookEditScreen::class.java)
        if (Util.checkModVersion("minecraft") { v -> v >= Version.parse("1.19.3") }) {
            addRetexturableScreen(HangingSignEditScreen::class.java)
        }
    }
}
