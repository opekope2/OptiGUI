package opekope2.optiglue

import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.MinecraftVersion
import net.minecraft.entity.Entity
import net.minecraft.entity.mob.SkeletonHorseEntity
import net.minecraft.entity.mob.ZombieHorseEntity
import net.minecraft.entity.passive.*
import net.minecraft.resource.ResourceType
import opekope2.optiglue.mc_1_19_3.RegistryLookupServiceImpl
import opekope2.optiglue.mc_1_19_3.ResourceResolverServiceImpl
import opekope2.optigui.EntryPoint
import opekope2.optigui.InitializerContext
import opekope2.optigui.internal.optifinecompat.processHorse
import opekope2.optigui.internal.service.EntityVariantLookupService
import opekope2.optigui.internal.service.OptiGlueService
import opekope2.optigui.service.RegistryLookupService
import opekope2.optigui.service.ResourceResolverService
import opekope2.optigui.service.registerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("unused")
object OptiGlueMod : EntryPoint, OptiGlueService, EntityVariantLookupService {
    internal val logger: Logger = LoggerFactory.getLogger("OptiGlue")

    override fun onInitialize(context: InitializerContext) {
        // Needed by OptiGUI
        registerService<RegistryLookupService>(RegistryLookupServiceImpl())
        registerService<ResourceResolverService>(ResourceResolverServiceImpl())
        registerService<OptiGlueService>(this)
        registerService<EntityVariantLookupService>(this)

        // Register preprocessor for camel in 1.19.3+
        // Camel filter factory is just horse filter factory, registered in OptiGUI
        context.registerPreprocessor<CamelEntity>(::processHorse)

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ResourceLoader)

        logger.info("OptiGlue $glueVersion initialized in Minecraft $minecraftVersion.")
    }

    override val glueVersion: String = "@mod_version@"
    override val minecraftVersion: String = MinecraftVersion.CURRENT.name

    override fun getVariant(entity: Entity): String? =
        when (entity) {
            is HorseEntity -> "horse"
            is DonkeyEntity -> "donkey"
            is MuleEntity -> "mule"
            is LlamaEntity -> "llama" // Includes trader llama
            is CamelEntity -> "_camel"
            is ZombieHorseEntity -> "_zombie_horse"
            is SkeletonHorseEntity -> "_skeleton_horse"
            else -> null
        }
}
