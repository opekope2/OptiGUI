package opekope2.optigui.interaction

import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import opekope2.optigui.interaction.data.IInteractionData
import opekope2.optigui.interaction.data.InteractionPlayerData
import opekope2.optigui.screen.ITextureChangeableScreen
import opekope2.optigui.util.INbtConvertible
import opekope2.optigui.util.subCompound
import java.time.LocalDateTime

/**
 * Interaction between a player and a block, entity, or item.
 *
 * @param screen The active GUI screen
 * @param data The details of the interaction
 */
data class Interaction(val screen: ITextureChangeableScreen, val data: IInteractionData) : INbtConvertible {
    /**
     * Details about the interacting player.
     */
    val playerData: InteractionPlayerData
        get() = data.playerData

    override fun optiGui_writeNbt(compound: NbtCompound, lookup: RegistryWrapper.WrapperLookup) {
        screen.optiGui_writeNbt(compound.subCompound("screen"), lookup)
        compound.put("time", createTimeNbt())
        data.optiGui_writeNbt(compound, lookup)
    }

    private fun createTimeNbt() = NbtCompound().apply {
        val now = LocalDateTime.now()
        putInt("year", now.year)
        putInt("month", now.month.value)
        putInt("day", now.dayOfMonth)
        putInt("weekday", now.dayOfWeek.value)
        putInt("hour", now.hour)
        putInt("minute", now.minute)
        putInt("second", now.second)
    }

    fun createNbt() = NbtCompound().also { optiGui_writeNbt(it, playerData.player.world.registryManager) }
}
