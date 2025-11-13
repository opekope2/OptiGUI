package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.screen_api.util.INbtConvertible

/**
 * Provides the screen NBT of an interaction.
 */
object ScreenNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: HolderLookup.Provider): Tag? {
        val screen = interaction.screen as? INbtConvertible ?: return null
        return CompoundTag().apply { screen.optiGui_writeNbt(this, lookup) }
    }
}
