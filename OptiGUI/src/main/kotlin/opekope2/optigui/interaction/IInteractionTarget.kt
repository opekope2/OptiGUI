package opekope2.optigui.interaction

import net.minecraft.block.BlockState
import net.minecraft.entity.EntityType
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import opekope2.optigui.util.INbtConvertible
import opekope2.optigui.util.identifier

/**
 * Represents the target the player interacts with.
 */
sealed interface IInteractionTarget : INbtConvertible {
    /**
     * Represents a block the player interacts with.
     *
     * @param id The registry ID of the block
     */
    data class Block(val id: Identifier) : IInteractionTarget {
        constructor(block: net.minecraft.block.Block) : this(block.identifier)
        constructor(blockState: BlockState) : this(blockState.block)

        override fun optiGui_writeNbt(compound: NbtCompound, lookup: RegistryWrapper.WrapperLookup) {
            compound.putString("type", "block")
            compound.putString("id", id.toString())
        }
    }

    /**
     * Represents an entity the player interacts with.
     *
     * @param id The registry ID of the entity
     */
    data class Entity(val id: Identifier) : IInteractionTarget {
        constructor(entityType: EntityType<*>) : this(entityType.identifier)
        constructor(entity: net.minecraft.entity.Entity) : this(entity.type)

        override fun optiGui_writeNbt(compound: NbtCompound, lookup: RegistryWrapper.WrapperLookup) {
            compound.putString("type", "entity")
            compound.putString("id", id.toString())
        }
    }

    /**
     * Represents an item the player interacts with.
     *
     * @param id The registry ID of the item
     */
    data class Item(val id: Identifier) : IInteractionTarget {
        constructor(item: net.minecraft.item.Item) : this(item.identifier)
        constructor(stack: ItemStack) : this(stack.item)

        override fun optiGui_writeNbt(compound: NbtCompound, lookup: RegistryWrapper.WrapperLookup) {
            compound.putString("type", "item")
            compound.putString("id", id.toString())
        }
    }

    /**
     * Represents an inventory screen (creative, survival, modded) the player interacts with.
     */
    data object Inventory : IInteractionTarget {
        override fun optiGui_writeNbt(compound: NbtCompound, lookup: RegistryWrapper.WrapperLookup) {
            compound.putString("type", "inventory")
        }
    }

    /**
     * Represents an unknown target the player interacts with.
     * This happens when an interaction is not initiated by a player action.
     */
    data object Unknown : IInteractionTarget {
        override fun optiGui_writeNbt(compound: NbtCompound, lookup: RegistryWrapper.WrapperLookup) {
            compound.putString("type", "unknown")
        }
    }
}
