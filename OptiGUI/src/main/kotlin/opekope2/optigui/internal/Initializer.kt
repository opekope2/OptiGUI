package opekope2.optigui.internal

import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.Version
import net.minecraft.client.gui.screen.ingame.BookEditScreen
import net.minecraft.client.gui.screen.ingame.BookScreen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen
import opekope2.lilac.api.Util
import opekope2.optigui.api.IEntrypoint
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.jvm.optionals.getOrNull

internal val logger: Logger = LoggerFactory.getLogger("OptiGUI")
val modVersion =
    FabricLoader.getInstance().getModContainer("optigui").getOrNull()?.metadata?.version
        ?: throw RuntimeException("OptiGUI is not loaded with id 'optigui'!")


@Suppress("unused")
fun initialize() {
    addRetexturableScreen(HandledScreen::class.java)
    addRetexturableScreen(BookScreen::class.java)
    addRetexturableScreen(BookEditScreen::class.java)
    if (Util.checkModVersion("minecraft") { v -> v >= Version.parse("1.19.3") }) {
        addRetexturableScreen(HangingSignEditScreen::class.java)
    }
}

@Suppress("unused")
fun runEntrypoints() {
    val entrypoints = Util.getEntrypointContainers(IEntrypoint::class.java)

    entrypoints.forEach { it.entrypoint.initialize(DummyInitializationContext()) }
}
