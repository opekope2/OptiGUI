package opekope2.optigui.internal

import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.loader.api.FabricLoader
import opekope2.optigui.EntryPoint
import opekope2.optigui.InitializerContext
import opekope2.optigui.internal.interaction.InteractionHandler
import opekope2.optigui.internal.service.OptiGlueService
import opekope2.optigui.internal.service.ResourceLoaderService
import opekope2.optigui.service.InteractionService
import opekope2.optigui.service.getServiceOrNull
import opekope2.optigui.service.registerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal val logger: Logger = LoggerFactory.getLogger("OptiGUI")
const val modVersion = "@mod_version@"

@Suppress("unused")
fun initialize() {
    registerService<ResourceLoaderService>(ResourceLoader) // Needed by OptiGlue
    registerService<InteractionService>(TextureReplacer)

    setupDevMessage()

    UseBlockCallback.EVENT.register(InteractionHandler)
    UseEntityCallback.EVENT.register(InteractionHandler)

    runEntryPoints()

    // Ensure OptiGlue loaded
    val glue = getServiceOrNull<OptiGlueService>() ?: throw RuntimeException("Error loading OptiGlue!")

    logger.info("OptiGUI $modVersion initialized in Minecraft ${glue.minecraftVersion}.")
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
