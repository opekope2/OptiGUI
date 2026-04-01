package opekope2.optigui.internal.selector

import net.minecraft.world.level.block.ChestBlock
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraft.world.level.block.state.properties.ChestType
import opekope2.optigui.filter.EqualityFilter
import opekope2.optigui.filter.PreProcessorFilter
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.selector.ISelector

internal class LargeChestSelector : ISelector {
    override fun createFilter(selector: String) = PreProcessorFilter.nullGuarded(
        ::isChestLarge,
        "Check if chest is large",
        null,
        EqualityFilter(selector.toBooleanStrict())
    )

    private fun isChestLarge(interaction: Interaction): Boolean? {
        val world = interaction.data.world
        val blockEntity = interaction.data.blockEntity as? ChestBlockEntity ?: return null
        val state = world.getBlockState(blockEntity.blockPos)
        return state.getValue(ChestBlock.TYPE) != ChestType.SINGLE
    }

    override fun getRawSelector(interaction: Interaction) = isChestLarge(interaction)?.toString()
}
