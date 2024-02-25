package opekope2.optigui.internal

import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screen.ingame.BookEditScreen
import net.minecraft.client.gui.screen.ingame.BookScreen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen
import net.minecraft.resource.ResourceType
import opekope2.optigui.EntryPoint
import opekope2.optigui.InitializerContext
import opekope2.optigui.internal.interaction.InteractionHandler
import opekope2.optigui.registry.RetexturableScreenRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.jvm.optionals.getOrNull

internal val logger: Logger = LoggerFactory.getLogger("OptiGUI")
val modVersion =
    FabricLoader.getInstance().getModContainer("optigui").getOrNull()?.metadata?.version
        ?: throw RuntimeException("OptiGUI is not loaded with id 'optigui'!")


@Suppress("unused")
fun initialize() {
    RetexturableScreenRegistry += HandledScreen::class.java
    RetexturableScreenRegistry += BookScreen::class.java
    RetexturableScreenRegistry += BookEditScreen::class.java
    RetexturableScreenRegistry += HangingSignEditScreen::class.java

    ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ResourceLoader)

    InteractionHandler

    runEntryPoints()

    logger.info("OptiGUI initialized.")
}

private fun runEntryPoints() {
    val entrypoints =
        FabricLoader.getInstance().getEntrypointContainers("optigui", /* Java moment */ EntryPoint::class.java)

    // Initialize OptiGlue first
    entrypoints.sortByDescending {
        when (it.provider.metadata.id) {
            "optigui" -> 2
            "optiglue" -> 1
            else -> 0
        }
    }

    entrypoints.forEach { it.entrypoint.onInitialize(InitializerContext(it.provider.metadata.id)) }
}
