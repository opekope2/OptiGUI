package opekope2.optigui

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.MinecraftVersion
import net.minecraft.block.entity.*
import opekope2.optigui.internal.InteractionHandler
import opekope2.optigui.internal.ResourceLoader
import opekope2.optigui.internal.glue.OptiGlue
import opekope2.optigui.provider.getProviderOrNull
import opekope2.optigui.provider.registerProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal val logger: Logger = LoggerFactory.getLogger("OptiGUI")
const val modVersion = "@mod_version@"
private val gameVersion = MinecraftVersion.CURRENT.name

@Suppress("unused")
fun initialize() {
    // Needed by OptiGlue
    registerProvider(ResourceLoader)

    opekope2.optigui.internal.mc_all.initialize(InitializerContext("optigui"))

    setupDevMessage()

    UseBlockCallback.EVENT.register(InteractionHandler)
    UseEntityCallback.EVENT.register(InteractionHandler)
    ClientTickEvents.END_WORLD_TICK.register(InteractionHandler)
    ClientPlayConnectionEvents.DISCONNECT.register(InteractionHandler)

    runEntryPoints()

    // Ensure OptiGlue loaded
    getProviderOrNull<OptiGlue>() ?: throw RuntimeException("Error loading OptiGlue!")

    logger.info("OptiGUI $modVersion initialized in Minecraft $gameVersion.")
}

private fun runEntryPoints() {
    val entrypoints =
        FabricLoader.getInstance().getEntrypointContainers("optigui", /* Java moment */ EntryPoint::class.java)

    // Initialize OptiGlue first
    entrypoints.sortByDescending { it.provider.metadata.id == "optiglue" }

    entrypoints.forEach { it.entrypoint.onInitialize(InitializerContext(it.provider.metadata.id)) }
}
