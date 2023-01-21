package opekope2.optiglue

import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.MinecraftVersion
import net.minecraft.resource.ResourceType
import opekope2.optigui.provider.RegistryLookup
import opekope2.optigui.provider.ResourceResolver
import opekope2.optigui.provider.registerProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal val logger: Logger = LoggerFactory.getLogger("OptiGlue")
const val modVersion = "@mod_version@"
private val gameVersion = MinecraftVersion.CURRENT.name

@Suppress("unused")
fun initialize() {
    // Needed by OptiGUI
    registerProvider<RegistryLookup>(RegistryLookupImpl())
    registerProvider<ResourceResolver>(ResourceResolverImpl())

    ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ResourceLoader)

    logger.info("OptiGlue $modVersion initialized in Minecraft $gameVersion.")
}
