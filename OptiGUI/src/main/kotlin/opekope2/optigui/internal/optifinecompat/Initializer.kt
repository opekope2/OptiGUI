package opekope2.optigui.internal.optifinecompat

import net.minecraft.block.entity.*
import net.minecraft.entity.mob.SkeletonHorseEntity
import net.minecraft.entity.mob.ZombieHorseEntity
import net.minecraft.entity.passive.*
import net.minecraft.entity.vehicle.ChestMinecartEntity
import opekope2.optigui.InitializerContext

internal fun initialize(context: InitializerContext) {
    // Register block entity filter factories
    context.registerFilterFactory(::createAnvilFilter)
    context.registerFilterFactory(::createBrewingStandFilter)
    context.registerFilterFactory(::createCraftingTableFilter)
    context.registerFilterFactory(::createEnchantingTableFilter)
    context.registerFilterFactory(::createFurnaceFilter)
    context.registerFilterFactory(::createHopperFilter)

    context.registerFilterFactory(::createChestFilter)
    context.registerFilterFactory(::createBeaconFilter)
    context.registerFilterFactory(::createDispenserFilter)
    context.registerFilterFactory(::createShulkerBoxFilter)

    context.registerFilterFactory(::createLoomFilter)
    context.registerFilterFactory(::createCartographyTableFilter)
    context.registerFilterFactory(::createGrindstoneFilter)
    context.registerFilterFactory(::createSmithingTableFilter)
    context.registerFilterFactory(::createStonecutterFilter)

    // Register inventory filter factories
    context.registerFilterFactory(::createSurvivalInventoryFilter)
    context.registerFilterFactory(::createCreativeInventoryFilter)

    // Register entity filter factories
    context.registerFilterFactory(::createVillagerFilter)
    context.registerFilterFactory(::createHorseFilter)

    // Register preprocessor for brewing stand
    context.registerPreprocessor<BrewingStandBlockEntity>(::processBrewingStand)

    // Register preprocessor for enchanting table
    context.registerPreprocessor<EnchantingTableBlockEntity>(::processEnchantingTable)

    // Register preprocessor for furnaces
    context.registerPreprocessor<FurnaceBlockEntity>(::processFurnace)
    context.registerPreprocessor<BlastFurnaceBlockEntity>(::processFurnace)
    context.registerPreprocessor<SmokerBlockEntity>(::processFurnace)

    // Register preprocessor for hopper
    context.registerPreprocessor<HopperBlockEntity>(::processHopper)

    // Register preprocessor for chests
    context.registerPreprocessor<ChestBlockEntity>(::processChest)
    context.registerPreprocessor<TrappedChestBlockEntity>(::processChest)
    context.registerPreprocessor<EnderChestBlockEntity>(::processChest)
    context.registerPreprocessor<BarrelBlockEntity>(::processChest)
    context.registerPreprocessor<ChestMinecartEntity>(::processChestMinecart)

    // Register preprocessor for beacon
    context.registerPreprocessor<BeaconBlockEntity>(::processBeacon)

    // Register preprocessor for villagers
    context.registerPreprocessor<VillagerEntity>(::processVillager)
    context.registerPreprocessor<WanderingTraderEntity>(::processVillager)

    // Register preprocessor for dispenser & dropper
    context.registerPreprocessor<DispenserBlockEntity>(::processDispenser)
    context.registerPreprocessor<DropperBlockEntity>(::processDispenser)

    // Register preprocessor for horses
    context.registerPreprocessor<HorseEntity>(::processHorse)
    context.registerPreprocessor<DonkeyEntity>(::processHorse)
    context.registerPreprocessor<MuleEntity>(::processHorse)
    context.registerPreprocessor<LlamaEntity>(::processHorse)
    context.registerPreprocessor<TraderLlamaEntity>(::processHorse)
    context.registerPreprocessor<ZombieHorseEntity>(::processHorse)
    context.registerPreprocessor<SkeletonHorseEntity>(::processHorse)

    // Register preprocessor for shulker boxes
    context.registerPreprocessor<ShulkerBoxBlockEntity>(::processShulkerBox)
}
