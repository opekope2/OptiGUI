package opekope2.optigui.interaction.nbt_provider

import net.minecraft.nbt.NbtElement
import net.minecraft.registry.RegistryWrapper
import opekope2.optigui.interaction.BlockInteraction
import opekope2.optigui.interaction.IInteraction

/**
 * Provides the block entity NBT of an interaction.
 */
object BlockEntityNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: RegistryWrapper.WrapperLookup): NbtElement? {
        val data = interaction as? BlockInteraction ?: return null
        return data.blockEntity?.createNbtWithId(lookup)
    }
}
