@file: JvmName("RegistryUtil")

package opekope2.optigui.util

import net.minecraft.world.level.block.Block
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Item
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import kotlin.jvm.optionals.getOrNull

/**
 * Finds the ID of the given block in the registry.
 */
val Block.identifier: ResourceLocation
    get() = BuiltInRegistries.BLOCK.getKey(this)

/**
 * Finds the ID of the given entity in the registry.
 */
val Entity.identifier: ResourceLocation
    get() = BuiltInRegistries.ENTITY_TYPE.getKey(type)

/**
 * Finds the ID of the given item in the registry.
 */
val Item.identifier: ResourceLocation
    get() = BuiltInRegistries.ITEM.getKey(this)

/**
 * Finds the biome ID at the given world position.
 *
 * @param pos The position to look up the biome
 */
fun Level.getBiomeId(pos: BlockPos) = getBiome(pos).unwrapKey().getOrNull()?.location()
    ?: throw RuntimeException("Cannot load biome at $pos in world $this!")
