package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.StringTag
import opekope2.optigui.interaction.IInteraction

/**
 * Provides the interaction hand NBT of an interaction.
 */
object HandNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, registryAccess: RegistryAccess) =
        StringTag.valueOf(interaction.hand.name)
}
