package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import opekope2.optigui.interaction.IInteraction

/**
 * Provides the ridden entity NBT of an interaction.
 */
object VehicleNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: HolderLookup.Provider) =
        interaction.vehicle?.saveWithoutId(CompoundTag())
}
