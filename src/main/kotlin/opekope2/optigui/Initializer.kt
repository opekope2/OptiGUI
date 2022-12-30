package opekope2.optigui

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resource.ResourceType
import opekope2.optigui.internal.InteractionHandler
import opekope2.optigui.internal.ResourceLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("OptiGUI")
const val version = "@mod_version@"

@Suppress("unused")
fun initialize() {
    ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ResourceLoader)
    UseBlockCallback.EVENT.register(InteractionHandler)
    UseEntityCallback.EVENT.register(InteractionHandler)
    ClientTickEvents.END_WORLD_TICK.register(InteractionHandler)
    ClientPlayConnectionEvents.DISCONNECT.register(InteractionHandler)

    logger.info("OptiGUI $version initialized.")
}
