package opekope2.optigui.interaction.nbt_provider

import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import opekope2.optigui.interaction.IInteraction

/**
 * Provides the player NBT of an interaction.
 */
object PlayerNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: RegistryWrapper.WrapperLookup): NbtCompound =
        interaction.player.writeNbt(NbtCompound())
}
