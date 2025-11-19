package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import opekope2.optigui.interaction.EntityInteraction
import opekope2.optigui.interaction.IInteraction

/**
 * Provides the entity NBT of an interaction.
 */
object EntityNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: HolderLookup.Provider): Tag? {
        val data = interaction as? EntityInteraction ?: return null
        return data.entity.saveWithoutId(CompoundTag())
    }
}
