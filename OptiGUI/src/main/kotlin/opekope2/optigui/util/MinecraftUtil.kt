package opekope2.optigui.util

import com.mojang.serialization.Encoder
import net.minecraft.client.Minecraft
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
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
 * Encodes [input] using [encoder] as NBT.
 *
 * @param T The type of [input]
 * @param input The data to encode
 * @param encoder The codec used to encode [input]
 * @param lookup The registry lookup to get [net.minecraft.nbt.NbtOps] from
 */
fun <T> encodeAsNbt(input: T, encoder: Encoder<T>, lookup: HolderLookup.Provider): Tag =
    encoder.encodeStart(lookup.createSerializationContext(NbtOps.INSTANCE), input).getOrThrow()

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
