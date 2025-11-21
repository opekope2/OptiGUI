package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.RegistryAccess
import opekope2.optigui.interaction.IInteraction

/**
 * Provides the item NBT of an interaction.
 */
object ItemNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, registryAccess: RegistryAccess) =
        interaction.item.saveOptional(registryAccess)
}
