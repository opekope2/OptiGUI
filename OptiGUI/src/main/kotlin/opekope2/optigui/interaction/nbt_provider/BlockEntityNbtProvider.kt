package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.Tag
import opekope2.optigui.interaction.BlockInteraction
import opekope2.optigui.interaction.IInteraction

/**
 * Provides the block entity NBT of an interaction.
 */
object BlockEntityNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, registryAccess: RegistryAccess): Tag? {
        val data = interaction as? BlockInteraction ?: return null
        return data.blockEntity?.saveWithId(registryAccess)
    }
}
