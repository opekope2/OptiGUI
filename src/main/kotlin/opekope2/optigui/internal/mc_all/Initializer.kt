package opekope2.optigui.internal.mc_all

import net.minecraft.block.entity.*
import net.minecraft.entity.passive.*
import opekope2.filter.registerFilterFactory
import opekope2.optigui.interaction.registerPreprocessor

internal fun initialize() {
    // Register block entity filter factories
    registerFilterFactory(::createChestFilter)
    registerFilterFactory(::createBeaconFilter)
    registerFilterFactory(::createDispenserFilter)
    registerFilterFactory(::createShulkerBoxFilter)

    // Register entity filter factories
    registerFilterFactory(::createVillagerFilter)

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

    // Register preprocessor for shulker boxes
    registerPreprocessor<ShulkerBoxBlockEntity>(::processShulkerBox)
}
