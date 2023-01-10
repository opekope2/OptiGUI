package opekope2.optigui.internal.mc_all

import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.block.entity.EnderChestBlockEntity
import net.minecraft.block.entity.TrappedChestBlockEntity
import opekope2.filter.registerFilterFactory
import opekope2.optigui.interaction.registerPreprocessor

internal fun initialize() {
    // Register filter factories
    registerFilterFactory(::createChestFilter)

    // Register preprocessor for chests
    registerPreprocessor<ChestBlockEntity>(::processChest)
    registerPreprocessor<TrappedChestBlockEntity>(::processChest)
    registerPreprocessor<EnderChestBlockEntity>(::processChest)
}
