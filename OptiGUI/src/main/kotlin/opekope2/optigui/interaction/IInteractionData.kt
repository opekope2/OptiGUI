package opekope2.optigui.interaction

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.Supplier

/**
 * Details about an interaction.
 */
sealed interface IInteractionData {
    /**
     * The identifier of the interacted container.
     */
    val id: Identifier

    /**
     * The interaction position.
     */
    val blockPos: BlockPos

    /**
     * The item the player interacted with.
     */
    val item: ItemStack

    /**
     * Details about the interacting player.
     */
    val playerData: Interaction.PlayerData

    /**
     * Extra details about the interaction. May be mutable.
     */
    val extraData: Supplier<NbtCompound>?

    /**
     * The world the interaction happened in.
     */
    val world: World
        get() = playerData.player.entityWorld
}
