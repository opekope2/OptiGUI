package opekope2.optigui.interaction.data

import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import opekope2.optigui.util.identifier
import java.util.function.Supplier

/**
 * Details about an interaction with an entity.
 *
 * @param entity The entity the player interacted with
 * @param item The item the player interacted with
 * @param playerData Details about the player
 * @param extraData Extra details about the interaction. May be mutable
 */
data class EntityInteractionData @JvmOverloads constructor(
    val entity: Entity,
    override val item: ItemStack,
    override val playerData: InteractionPlayerData,
    override val extraData: Supplier<NbtCompound>? = null
) : IInteractionData {
    override val id: Identifier
        get() = entity.identifier

    override val blockPos: BlockPos
        get() = entity.blockPos
}
