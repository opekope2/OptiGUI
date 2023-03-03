package opekope2.optigui

import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.MinecraftVersion
import net.minecraft.block.entity.*
import opekope2.optigui.internal.InteractionHandler
import opekope2.optigui.internal.ResourceLoader
import opekope2.optigui.internal.TextureReplacer
import opekope2.optigui.internal.service.OptiGlueService
import opekope2.optigui.internal.service.ResourceLoaderService
import opekope2.optigui.service.InteractionService
import opekope2.optigui.service.getServiceOrNull
import opekope2.optigui.service.registerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal val logger: Logger = LoggerFactory.getLogger("OptiGUI")
const val modVersion = "@mod_version@"
private val gameVersion = MinecraftVersion.CURRENT.name

@Suppress("unused")
fun initialize() {
    registerService<ResourceLoaderService>(ResourceLoader) // Needed by OptiGlue
    registerService<InteractionService>(TextureReplacer)

    setupDevMessage()

    UseBlockCallback.EVENT.register(InteractionHandler)
    UseEntityCallback.EVENT.register(InteractionHandler)

    opekope2.optigui.internal.optifinecompat.initialize(InitializerContext("optigui"))

    runEntryPoints()

    // Ensure OptiGlue loaded
    getServiceOrNull<OptiGlueService>() ?: throw RuntimeException("Error loading OptiGlue!")

    logger.info("OptiGUI $modVersion initialized in Minecraft $gameVersion.")
}

private fun runEntryPoints() {
    val entrypoints =
        FabricLoader.getInstance().getEntrypointContainers("optigui", /* Java moment */ EntryPoint::class.java)

    // Initialize OptiGlue first
    entrypoints.sortByDescending { it.provider.metadata.id == "optiglue" }

    entrypoints.forEach { it.entrypoint.onInitialize(InitializerContext(it.provider.metadata.id)) }
}
