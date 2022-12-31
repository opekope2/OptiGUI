package opekope2.optigui

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.MinecraftVersion
import net.minecraft.resource.ResourceType
import opekope2.optigui.exception.UnsupportedMinecraftVersionException
import opekope2.optigui.internal.InteractionHandler
import opekope2.optigui.internal.ResourceLoader
import opekope2.optigui.provider.IRegistryLookupProvider
import opekope2.optigui.provider.IResourceManagerProvider
import opekope2.optigui.provider.registerProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal val logger: Logger = LoggerFactory.getLogger("OptiGUI")
const val version = "@mod_version@"
private val gameVersion = MinecraftVersion.CURRENT.name

@Suppress("unused")
fun initialize() {
    loadProviders()

    ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ResourceLoader)
    UseBlockCallback.EVENT.register(InteractionHandler)
    UseEntityCallback.EVENT.register(InteractionHandler)
    ClientTickEvents.END_WORLD_TICK.register(InteractionHandler)
    ClientPlayConnectionEvents.DISCONNECT.register(InteractionHandler)

    logger.info("OptiGUI $version initialized.")
}

private fun loadProviders() {
    when (gameVersion) {
        "1.19.3" -> {
            registerProvider<IResourceManagerProvider>(opekope2.optigui.internal.mc_1_19_3.ResourceManagerProvider())
            registerProvider<IRegistryLookupProvider>(opekope2.optigui.internal.mc_1_19_3.RegistryLookupProvider())
        }

        else -> throw UnsupportedMinecraftVersionException(version, gameVersion)
    }
}

