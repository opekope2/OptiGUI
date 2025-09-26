package opekope2.optigui.interaction.nbt_provider

import net.minecraft.nbt.NbtElement
import net.minecraft.registry.RegistryWrapper
import opekope2.optigui.interaction.IInteraction

/**
 * Provides the item NBT of an interaction.
 */
object ItemNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: RegistryWrapper.WrapperLookup): NbtElement =
        interaction.item.encodeAllowEmpty(lookup)
}
