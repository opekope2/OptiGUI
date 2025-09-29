package opekope2.optigui.interaction.data

import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.math.BlockPos
import opekope2.optigui.interaction.IInteractionTarget
import opekope2.optigui.util.subCompound

/**
 * Details about an interaction with an entity.
 *
 * @param entity The entity the player interacted with
 * @param item The item the player interacted with
 * @param playerData Details about the player
 */
data class EntityInteractionData(
    val entity: Entity,
    override val item: ItemStack,
    override val playerData: InteractionPlayerData
) : IInteractionData {
    override val target = IInteractionTarget.Entity(entity)

    override val blockPos: BlockPos
        get() = entity.blockPos

    override fun optiGui_writeNbt(compound: NbtCompound, lookup: RegistryWrapper.WrapperLookup) {
        super.optiGui_writeNbt(compound, lookup)

        entity.writeNbt(compound.subCompound("entity"))
    }
}
