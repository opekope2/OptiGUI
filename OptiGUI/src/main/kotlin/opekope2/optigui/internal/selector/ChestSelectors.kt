package opekope2.optigui.internal.selector

import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.block.enums.ChestType
import net.minecraft.state.property.EnumProperty
import opekope2.optigui.filter.EqualityFilter
import opekope2.optigui.filter.IFilter.Result.Companion.mismatch
import opekope2.optigui.filter.PreProcessorFilter
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.selector.ISelector

internal class LargeChestSelector : ISelector {
    override fun createFilter(selector: String) = PreProcessorFilter.nullGuarded(
        ::isChestLarge,
        "Check if chest is large",
        mismatch(),
        EqualityFilter(selector.toBooleanStrict())
    )

    private fun isChestLarge(interaction: Interaction): Boolean? {
        val world = interaction.data.world
        val blockEntity = interaction.data.blockEntity as? ChestBlockEntity ?: return null
        val state = world.getBlockState(blockEntity.pos)
        return state.entries[chestTypeEnum] != ChestType.SINGLE
    }

    override fun getRawSelector(interaction: Interaction) = isChestLarge(interaction)?.toString()

    private companion object {
        private val chestTypeEnum = EnumProperty.of("type", ChestType::class.java)
    }
}
