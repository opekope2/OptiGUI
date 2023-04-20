package opekope2.optiglue

import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.MinecraftVersion
import net.minecraft.entity.vehicle.ChestBoatEntity
import net.minecraft.resource.ResourceType
import opekope2.optiglue.mc_1_19.GlueResource
import opekope2.optiglue.mc_1_19.RegistryLookupServiceImpl
import opekope2.optigui.EntryPoint
import opekope2.optigui.InitializerContext
import opekope2.optigui.internal.processCommon
import opekope2.optigui.internal.service.OptiGlueService
import opekope2.optigui.service.RegistryLookupService
import opekope2.optigui.service.ResourceAccessService
import opekope2.optigui.service.registerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("unused")
object OptiGlueMod : EntryPoint, OptiGlueService {
    internal val logger: Logger = LoggerFactory.getLogger("OptiGlue")

    override fun onInitialize(context: InitializerContext) {
        // Needed by OptiGUI
        registerService<RegistryLookupService>(RegistryLookupServiceImpl())
        registerService<ResourceAccessService>(GlueResource.Companion)
        registerService<OptiGlueService>(this)

        context.registerPreprocessor<ChestBoatEntity>(::processCommon)

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ResourceLoader)

        logger.info("OptiGlue $glueVersion initialized in Minecraft $minecraftVersion.")
    }

    override val glueVersion: String = "@mod_version@"
    override val minecraftVersion: String = MinecraftVersion.CURRENT.name
}
