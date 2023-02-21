package opekope2.optiglue

import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.MinecraftVersion
import net.minecraft.entity.mob.SkeletonHorseEntity
import net.minecraft.entity.mob.ZombieHorseEntity
import net.minecraft.entity.passive.*
import net.minecraft.resource.ResourceType
import opekope2.optiglue.mc_1_18_2.RegistryLookupServiceImpl
import opekope2.optiglue.mc_1_18_2.ResourceResolverServiceImpl
import opekope2.optigui.EntryPoint
import opekope2.optigui.InitializerContext
import opekope2.optigui.internal.service.HorseVariantLookupService
import opekope2.optigui.internal.service.OptiGlueService
import opekope2.optigui.service.RegistryLookupService
import opekope2.optigui.service.ResourceResolverService
import opekope2.optigui.service.registerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("unused")
object OptiGlueMod : EntryPoint, OptiGlueService, HorseVariantLookupService {
    internal val logger: Logger = LoggerFactory.getLogger("OptiGlue")
    private val gameVersion = MinecraftVersion.CURRENT.name

    override fun onInitialize(context: InitializerContext) {
        // Needed by OptiGUI
        registerService<RegistryLookupService>(RegistryLookupServiceImpl())
        registerService<ResourceResolverService>(ResourceResolverServiceImpl())
        registerService<OptiGlueService>(this)
        registerService<HorseVariantLookupService>(this)

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ResourceLoader)

        logger.info("OptiGlue $version initialized in Minecraft $gameVersion.")
    }

    override val version: String = "@mod_version@"

    override fun getHorseVariant(horse: AbstractHorseEntity): String? =
        when (horse) {
            is HorseEntity -> "horse"
            is DonkeyEntity -> "donkey"
            is MuleEntity -> "mule"
            is LlamaEntity -> "llama" // Includes trader llama
            is ZombieHorseEntity -> "_zombie_horse"
            is SkeletonHorseEntity -> "_skeleton_horse"
            else -> null
        }
}
