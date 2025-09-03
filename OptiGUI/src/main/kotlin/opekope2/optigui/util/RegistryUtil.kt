@file:JvmName("RegistryUtil")

package opekope2.optigui.util

import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Finds the ID of the given block in the registry.
 */
val Block.identifier: Identifier
    get() = Registries.BLOCK.getId(this)

/**
 * Finds the ID of the given entity in the registry.
 */
val Entity.identifier: Identifier
    get() = type.identifier

/**
 * Finds the ID of the given entity type in the registry.
 */
val EntityType<*>.identifier: Identifier
    get() = Registries.ENTITY_TYPE.getId(this)

/**
 * Finds the ID of the given item in the registry.
 */
val Item.identifier: Identifier
    get() = Registries.ITEM.getId(this)

/**
 * Finds the biome ID at the given world position.
 *
 * @param pos The position to look up the biome
 */
fun World.getBiomeId(pos: BlockPos): Identifier = getBiome(pos).key.get().value
