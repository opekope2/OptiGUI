package opekope2.optigui

import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resource.ResourceType
import opekope2.optigui.internal.ResourceLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("OptiGUI")
const val version = "@mod_version@"

@Suppress("unused")
fun initialize() {
    ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ResourceLoader)

    logger.info("OptiGUI $version initialized.")
}
