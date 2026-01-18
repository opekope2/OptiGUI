package opekope2.optigui.interaction

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import opekope2.optigui.interaction.nbt_provider.IInteractionNbtProvider
import opekope2.optigui.screen_api.screen.ITextureChangeableScreen
import java.util.function.Function

/**
 * Details about an interaction.
 */
sealed interface IInteraction {
    /**
     * The active GUI screen.
     */
    val screen: ITextureChangeableScreen

    /**
     * The target of the interaction.
     */
    val target: InteractionTarget

    /**
     * The interaction position.
     */
    val blockPos: BlockPos

    /**
     * The item the player interacted with.
     */
    val item: ItemStack

    /**
     * The interacting player.
     */
    val player: Player

    /**
     * The hand the player interacted with.
     */
    val hand: InteractionHand

    /**
     * The world the interaction happened in.
     */
    val world: Level
        get() = player.commandSenderWorld

    /**
     * Converts the interaction to NBT for filtering.
     */
    fun createNbt() = CompoundTag().also {
        val registryAccess = player.level().registryAccess()
        for ((key, value) in IInteractionNbtProvider.Registry) {
            it.put(key, value.get(this, registryAccess) ?: continue)
        }
    }

    /**
     * A factory interface that creates an [IInteraction] from an [ITextureChangeableScreen].
     */
    fun interface IFactory : Function<ITextureChangeableScreen, IInteraction>
}
