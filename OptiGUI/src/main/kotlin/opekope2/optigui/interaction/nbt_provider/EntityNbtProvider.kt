package opekope2.optigui.interaction.nbt_provider

import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.registry.RegistryWrapper
import opekope2.optigui.interaction.EntityInteraction
import opekope2.optigui.interaction.IInteraction

/**
 * Provides the entity NBT of an interaction.
 */
object EntityNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: RegistryWrapper.WrapperLookup): NbtElement? {
        val data = interaction as? EntityInteraction ?: return null
        return data.entity.writeNbt(NbtCompound())
    }
}
