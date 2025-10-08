package opekope2.optigui.interaction.nbt_provider

import net.minecraft.nbt.NbtElement
import net.minecraft.registry.RegistryWrapper
import opekope2.optigui.interaction.IInteraction

/**
 * Provides the NBT of the structures the player is in.
 */
object StructureBoundingBoxProvider : IInteractionNbtProvider {
    // TODO implement
    override fun get(interaction: IInteraction, lookup: RegistryWrapper.WrapperLookup): NbtElement? = null
}
