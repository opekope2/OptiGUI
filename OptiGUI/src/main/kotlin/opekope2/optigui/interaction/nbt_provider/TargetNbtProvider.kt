package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.CompoundTag
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.interaction.InteractionTarget

/**
 * Provides the target NBT of an interaction.
 */
object TargetNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, registryAccess: RegistryAccess) = CompoundTag().apply {
        val target = interaction.target
        putString("type", target.type)
        if (target is InteractionTarget.Block) putString("id", target.id.toString())
        if (target is InteractionTarget.Entity) putString("id", target.id.toString())
        if (target is InteractionTarget.Item) putString("id", target.id.toString())
    }
}
