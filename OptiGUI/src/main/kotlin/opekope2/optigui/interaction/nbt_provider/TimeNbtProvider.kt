package opekope2.optigui.interaction.nbt_provider

import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import opekope2.optigui.interaction.IInteraction
import java.time.LocalDateTime

/**
 * Provides the time NBT of an interaction.
 */
object TimeNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: RegistryWrapper.WrapperLookup) = NbtCompound().apply {
        val now = LocalDateTime.now()
        putInt("year", now.year)
        putInt("month", now.month.value)
        putInt("day", now.dayOfMonth)
        putInt("weekday", now.dayOfWeek.value)
        putInt("hour", now.hour)
        putInt("minute", now.minute)
        putInt("second", now.second)
    }
}
