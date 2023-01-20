package opekope2.optigui.internal.mc_all

import net.minecraft.block.entity.*
import net.minecraft.entity.mob.SkeletonHorseEntity
import net.minecraft.entity.mob.ZombieHorseEntity
import net.minecraft.entity.passive.*
import opekope2.filter.registerFilterFactory
import opekope2.optigui.interaction.registerPreprocessor

internal fun initialize() {
    // Register block entity filter factories
    registerFilterFactory(::createAnvilFilter)
    registerFilterFactory(::createBrewingStandFilter)
    registerFilterFactory(::createCraftingTableFilter)
    registerFilterFactory(::createEnchantingTableFilter)
    registerFilterFactory(::createFurnaceFilter)
    registerFilterFactory(::createHopperFilter)

    registerFilterFactory(::createChestFilter)
    registerFilterFactory(::createBeaconFilter)
    registerFilterFactory(::createDispenserFilter)
    registerFilterFactory(::createShulkerBoxFilter)

    // Register inventory filter factories
    registerFilterFactory(::createSurvivalInventoryFilter)
    registerFilterFactory(::createCreativeInventoryFilter)

    // Register entity filter factories
    registerFilterFactory(::createVillagerFilter)
    registerFilterFactory(::createHorseFilter)

    // Register preprocessor for brewing stand
    registerPreprocessor<BrewingStandBlockEntity>(::processBrewingStand)

    // Register preprocessor for enchanting table
    registerPreprocessor<EnchantingTableBlockEntity>(::processEnchantingTable)

    // Register preprocessor for furnaces
    registerPreprocessor<FurnaceBlockEntity>(::processFurnace)
    registerPreprocessor<BlastFurnaceBlockEntity>(::processFurnace)
    registerPreprocessor<SmokerBlockEntity>(::processFurnace)

    // Register preprocessor for hopper
    registerPreprocessor<HopperBlockEntity>(::processHopper)

    // Register preprocessor for chests
    registerPreprocessor<ChestBlockEntity>(::processChest)
    registerPreprocessor<TrappedChestBlockEntity>(::processChest)
    registerPreprocessor<EnderChestBlockEntity>(::processChest)
    registerPreprocessor<BarrelBlockEntity>(::processChest)

    // Register preprocessor for beacon
    registerPreprocessor<BeaconBlockEntity>(::processBeacon)

    // Register preprocessor for villagers
    registerPreprocessor<VillagerEntity>(::processVillager)
    registerPreprocessor<WanderingTraderEntity>(::processVillager)

    // Register preprocessor for dispenser & dropper
    registerPreprocessor<DispenserBlockEntity>(::processDispenser)
    registerPreprocessor<DropperBlockEntity>(::processDispenser)

    // Register preprocessor for horses
    registerPreprocessor<HorseEntity>(::processHorse)
    registerPreprocessor<DonkeyEntity>(::processHorse)
    registerPreprocessor<MuleEntity>(::processHorse)
    registerPreprocessor<LlamaEntity>(::processHorse)
    registerPreprocessor<TraderLlamaEntity>(::processHorse)
    registerPreprocessor<CamelEntity>(::processHorse)
    registerPreprocessor<ZombieHorseEntity>(::processHorse)
    registerPreprocessor<SkeletonHorseEntity>(::processHorse)

    // Register preprocessor for shulker boxes
    registerPreprocessor<ShulkerBoxBlockEntity>(::processShulkerBox)
}
