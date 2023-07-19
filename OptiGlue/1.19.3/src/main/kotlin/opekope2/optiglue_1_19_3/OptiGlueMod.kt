package opekope2.optiglue_1_19_3

import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.block.entity.HangingSignBlockEntity
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen
import net.minecraft.entity.passive.CamelEntity
import net.minecraft.entity.vehicle.ChestBoatEntity
import net.minecraft.resource.ResourceType
import net.minecraft.util.Nameable
import opekope2.optigui.EntryPoint
import opekope2.optigui.InitializerContext
import opekope2.optigui.internal.processCommon
import opekope2.optigui.internal.service.RetexturableScreensRegistryService
import opekope2.optigui.properties.ChestBoatProperties
import opekope2.optigui.properties.DefaultProperties
import opekope2.optigui.service.RegistryLookupService
import opekope2.optigui.service.ResourceAccessService
import opekope2.optigui.service.getService
import opekope2.optigui.service.registerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("unused")
object OptiGlueMod : EntryPoint {
    internal val logger: Logger = LoggerFactory.getLogger("OptiGlue")
    private val lookup: RegistryLookupService by lazy(::getService)

    override fun onInitialize(context: InitializerContext) {
        // Needed by OptiGUI
        registerService<RegistryLookupService>(RegistryLookupServiceImpl())
        registerService<ResourceAccessService>(GlueResource.Companion)

        getService<RetexturableScreensRegistryService>().addRetexturableScreen(HangingSignEditScreen::class.java)

        context.registerPreprocessor<ChestBoatEntity>(::processChestBoat)
        context.registerPreprocessor<CamelEntity>(::processCommon)

        context.registerPreprocessor<HangingSignBlockEntity>(::processHangingSign)

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ResourceLoader)

        logger.info("OptiGlue initialized.")
    }

    private fun processChestBoat(chestBoat: ChestBoatEntity): Any? {
        val world = chestBoat.entityWorld ?: return null

        return ChestBoatProperties(
            container = lookup.lookupEntityId(chestBoat),
            name = (chestBoat as? Nameable)?.customName?.string,
            biome = lookup.lookupBiomeId(world, chestBoat.blockPos),
            height = chestBoat.blockY,
            variant = chestBoat.variant.getName()
        )
    }


    private fun processHangingSign(sign: HangingSignBlockEntity): Any? {
        val world = sign.world ?: return null

        return DefaultProperties(
            container = lookup.lookupBlockId(world.getBlockState(sign.pos).block),
            name = null,
            biome = lookup.lookupBiomeId(world, sign.pos),
            height = sign.pos.y
        )
    }
}
