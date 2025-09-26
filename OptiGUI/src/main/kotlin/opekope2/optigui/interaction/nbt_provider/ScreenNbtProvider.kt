package opekope2.optigui.interaction.nbt_provider

import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.registry.RegistryWrapper
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.util.INbtConvertible

/**
 * Provides the screen NBT of an interaction.
 */
object ScreenNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: RegistryWrapper.WrapperLookup): NbtElement? {
        val screen = interaction.screen as? INbtConvertible ?: return null
        return NbtCompound().apply { screen.optiGui_writeNbt(this, lookup) }
    }
}
