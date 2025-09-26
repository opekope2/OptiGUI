package opekope2.optigui.interaction.nbt_provider

import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import opekope2.optigui.interaction.IInteraction

/**
 * Provides the ridden entity NBT of an interaction.
 */
object VehicleNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: RegistryWrapper.WrapperLookup) =
        interaction.vehicle?.writeNbt(NbtCompound())
}
