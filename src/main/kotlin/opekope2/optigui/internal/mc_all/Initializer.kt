package opekope2.optigui.internal.mc_all

import net.minecraft.block.entity.*
import opekope2.filter.registerFilterFactory
import opekope2.optigui.interaction.registerPreprocessor

internal fun initialize() {
    // Register filter factories
    registerFilterFactory(::createChestFilter)
    registerFilterFactory(::createBeaconFilter)

    // Register preprocessor for chests
    registerPreprocessor<ChestBlockEntity>(::processChest)
    registerPreprocessor<TrappedChestBlockEntity>(::processChest)
    registerPreprocessor<EnderChestBlockEntity>(::processChest)

    // Register preprocessor for beacon
    registerPreprocessor<BeaconBlockEntity>(::processBeacon)
}
