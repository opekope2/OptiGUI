package opekope2.optigui

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.MinecraftVersion
import net.minecraft.block.entity.*
import net.minecraft.resource.ResourceType
import opekope2.optigui.exception.UnsupportedMinecraftVersionException
import opekope2.optigui.internal.InteractionHandler
import opekope2.optigui.internal.ResourceLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal val logger: Logger = LoggerFactory.getLogger("OptiGUI")
const val modVersion = "@mod_version@"
private val gameVersion = MinecraftVersion.CURRENT.name

@Suppress("unused")
fun initialize() {
    when (gameVersion) {
        "1.19.3" -> opekope2.optigui.internal.mc_1_19_3.initialize()
        else -> throw UnsupportedMinecraftVersionException(modVersion, gameVersion)
    }
    opekope2.optigui.internal.mc_all.initialize()

    ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ResourceLoader)
    UseBlockCallback.EVENT.register(InteractionHandler)
    UseEntityCallback.EVENT.register(InteractionHandler)
    ClientTickEvents.END_WORLD_TICK.register(InteractionHandler)
    ClientPlayConnectionEvents.DISCONNECT.register(InteractionHandler)

    logger.info("OptiGUI $modVersion initialized.")
}
