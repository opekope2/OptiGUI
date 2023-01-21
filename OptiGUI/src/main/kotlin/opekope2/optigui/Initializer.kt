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
import opekope2.optigui.provider.registerProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal val logger: Logger = LoggerFactory.getLogger("OptiGUI")
const val modVersion = "@mod_version@"
private val gameVersion = MinecraftVersion.CURRENT.name

@Suppress("unused")
fun initialize() {
    registerProvider(ResourceLoader) // OptiGlue magic

    opekope2.optigui.internal.mc_all.initialize()

    registerDevMessage()

    UseBlockCallback.EVENT.register(InteractionHandler)
    UseEntityCallback.EVENT.register(InteractionHandler)
    ClientTickEvents.END_WORLD_TICK.register(InteractionHandler)
    ClientPlayConnectionEvents.DISCONNECT.register(InteractionHandler)

    logger.info("OptiGUI $modVersion initialized.")

    runEntryPoints()
}

private fun runEntryPoints() =
    FabricLoader.getInstance().getEntrypoints("optigui", /* Java moment */ EntryPoint::class.java)
        .forEach(EntryPoint::run)
