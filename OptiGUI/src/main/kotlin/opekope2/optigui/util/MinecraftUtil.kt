package opekope2.optigui.util

import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

/**
 * @see Minecraft.getInstance
 */
inline val mc get() = Minecraft.getInstance()

/**
 * Finds the ID of the given block in the registry.
 */
val Block.identifier: ResourceLocation
    get() = BuiltInRegistries.BLOCK.getKey(this)

/**
 * Finds the ID of the given entity in the registry.
 */
val Entity.identifier: ResourceLocation
    get() = type.identifier

/**
 * Finds the ID of the given entity type in the registry.
 */
val EntityType<*>.identifier: ResourceLocation
    get() = BuiltInRegistries.ENTITY_TYPE.getKey(this)

/**
 * Finds the ID of the given item in the registry.
 */
val Item.identifier: ResourceLocation
    get() = BuiltInRegistries.ITEM.getKey(this)

