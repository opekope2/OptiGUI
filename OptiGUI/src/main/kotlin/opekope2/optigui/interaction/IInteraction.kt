package opekope2.optigui.interaction

import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import opekope2.optigui.interaction.nbt_provider.IInteractionNbtProvider
import opekope2.optigui.screen.ITextureChangeableScreen
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
    val player: PlayerEntity

    /**
     * The hand the player interacted with.
     */
    val hand: Hand

    /**
     * The world the interaction happened in.
     */
    val world: World
        get() = player.entityWorld

    /**
     * The entity the player is rinding or `null`, if the player is not riding anything.
     */
    val vehicle: Entity?
        get() = player.vehicle

    /**
     * Converts the interaction to NBT for filtering.
     */
    fun createNbt() = NbtCompound().also {
        val lookup = player.world.registryManager
        for ((key, value) in IInteractionNbtProvider.Registry) {
            it.put(key, value.get(this, lookup) ?: continue)
        }
    }

    /**
     * A factory interface that creates an [IInteraction] from an [ITextureChangeableScreen].
     */
    fun interface IFactory : Function<ITextureChangeableScreen, IInteraction>
}
