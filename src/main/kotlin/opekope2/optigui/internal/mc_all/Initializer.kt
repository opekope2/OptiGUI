package opekope2.optigui.internal.mc_all

import net.minecraft.block.entity.*
import net.minecraft.entity.passive.*
import opekope2.filter.registerFilterFactory
import opekope2.optigui.interaction.registerPreprocessor

internal fun initialize() {
    // Register block entity filter factories
    registerFilterFactory(::createChestFilter)
    registerFilterFactory(::createBeaconFilter)

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
}
