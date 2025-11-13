package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.Tag
import opekope2.optigui.interaction.IInteraction

/**
 * Provides the NBT of the structures the player is in.
 */
object StructureBoundingBoxProvider : IInteractionNbtProvider {
    // TODO implement
    override fun get(interaction: IInteraction, lookup: HolderLookup.Provider): Tag? = null
}
