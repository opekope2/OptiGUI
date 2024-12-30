package opekope2.optigui.interaction

import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import opekope2.optigui.interaction.data.IInteractionData
import opekope2.optigui.interaction.data.InteractionPlayerData
import opekope2.optigui.screen.IRetexturableScreen
import opekope2.optigui.util.INbtConvertible
import opekope2.optigui.util.subCompound
import java.time.LocalDateTime

/**
 * Interaction between a player and a container.
 *
 * @param originalTexture The texture to be replaced.
 * @param screen The active GUI screen
 * @param data The details of the interaction
 */
data class Interaction(
    val originalTexture: Identifier,
    val screen: IRetexturableScreen,
    val data: IInteractionData
) : INbtConvertible {
    /**
     * Details about the interacting player.
     */
    val playerData: InteractionPlayerData
        get() = data.playerData

    override fun writeNbt(compound: NbtCompound, lookup: RegistryWrapper.WrapperLookup) {
        compound.putString("original_texture", originalTexture.toString())
        screen.optiGui_writeNbt(compound.subCompound("screen"), lookup)
        compound.put("time", createTimeNbt())
        data.writeNbt(compound, lookup)
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

    fun createNbt() = NbtCompound().also { writeNbt(it, playerData.player.world.registryManager) }
}
