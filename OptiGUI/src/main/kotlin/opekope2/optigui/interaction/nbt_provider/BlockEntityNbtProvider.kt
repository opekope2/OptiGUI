package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.Tag
import opekope2.optigui.interaction.BlockInteraction
import opekope2.optigui.interaction.IInteraction

/**
 * Provides the block entity NBT of an interaction.
 */
object BlockEntityNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: HolderLookup.Provider): Tag? {
        val data = interaction as? BlockInteraction ?: return null
        return data.blockEntity?.saveWithId(lookup)
    }
}
