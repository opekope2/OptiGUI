package opekope2.optigui.interaction.nbt_provider

import net.minecraft.nbt.NbtString
import net.minecraft.registry.RegistryWrapper
import opekope2.optigui.interaction.IInteraction

/**
 * Provides the interaction hand NBT of an interaction.
 */
object HandNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: RegistryWrapper.WrapperLookup): NbtString =
        NbtString.of(interaction.hand.name)
}
