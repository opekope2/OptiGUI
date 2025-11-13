package opekope2.optigui.interaction

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import opekope2.optigui.util.identifier

/**
 * Represents the target the player interacts with.
 */
sealed class InteractionTarget(val type: String) {
    /**
     * Represents a block the player interacts with.
     *
     * @param id The registry ID of the block
     */
    data class Block(val id: ResourceLocation) : InteractionTarget("block") {
        constructor(block: net.minecraft.world.level.block.Block) : this(block.identifier)
        constructor(blockState: BlockState) : this(blockState.block)
    }

    /**
     * Represents an entity the player interacts with.
     *
     * @param id The registry ID of the entity
     */
    data class Entity(val id: ResourceLocation) : InteractionTarget("entity") {
        constructor(entityType: EntityType<*>) : this(entityType.identifier)
        constructor(entity: net.minecraft.world.entity.Entity) : this(entity.type)
    }

    /**
     * Represents an item the player interacts with.
     *
     * @param id The registry ID of the item
     */
    data class Item(val id: ResourceLocation) : InteractionTarget("item") {
        constructor(item: net.minecraft.world.item.Item) : this(item.identifier)
        constructor(stack: ItemStack) : this(stack.item)
    }

    /**
     * Represents an inventory screen (creative, survival, modded) the player interacts with.
     */
    data object Inventory : InteractionTarget("inventory")

    /**
     * Represents an unknown target the player interacts with.
     * This happens when an interaction is not initiated by a player action.
     */
    data object Unknown : InteractionTarget("unknown")
}
