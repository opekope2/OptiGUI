package opekope2.optigui.internal

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screen.ingame.BookEditScreen
import net.minecraft.client.gui.screen.ingame.BookScreen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import opekope2.optigui.EntryPoint
import opekope2.optigui.InitializerContext
import opekope2.optigui.internal.interaction.InteractionHandler
import opekope2.optigui.internal.service.ResourceLoaderService
import opekope2.optigui.internal.service.RetexturableScreensRegistryService
import opekope2.optigui.service.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.jvm.optionals.getOrNull

internal val logger: Logger = LoggerFactory.getLogger("OptiGUI")
val modVersion =
    FabricLoader.getInstance().getModContainer("optigui").getOrNull()?.metadata?.version
        ?: throw RuntimeException("OptiGUI is not loaded with id 'optigui'!")


@Suppress("unused")
fun initialize() {
    registerService<ResourceLoaderService>(ResourceLoader) // Needed by OptiGlue
    registerService<InteractionService>(TextureReplacer)

    RetexturableScreensRegistry().apply {
        registerService<RetexturableScreensRegistryService>(this)

        addRetexturableScreen(HandledScreen::class.java)
        addRetexturableScreen(BookScreen::class.java)
        addRetexturableScreen(BookEditScreen::class.java)
    }

    InteractionHandler

    runEntryPoints()

    // Ensure OptiGlue loaded
    getServiceOrNull<RegistryLookupService>()
        ?: throw RuntimeException("RegistryLookupService hasn't been registered by OptiGlue!")
    getServiceOrNull<ResourceAccessService>()
        ?: throw RuntimeException("ResourceAccessService hasn't been registered by OptiGlue!")

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
