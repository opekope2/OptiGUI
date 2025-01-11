@file: JvmName("NbtUtil")

package opekope2.optigui.util

import com.mojang.serialization.DynamicOps
import com.mojang.serialization.Encoder
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtOps
import net.minecraft.registry.RegistryWrapper

/**
 * Encodes [input] using [encoder], and puts it into an [NbtCompound].
 *
 * @param T The type of [input]
 * @param key The key in the receiver [NbtCompound] to associate [input] with
 * @param input The data to encode
 * @param encoder The codec used to encode [input]
 * @param lookup The registry lookup to get NBT Ops from
 */
fun <T> NbtCompound.encode(key: String, input: T, encoder: Encoder<T>, lookup: RegistryWrapper.WrapperLookup) {
    encode(key, input, encoder, NbtOps.INSTANCE)
}

/**
 * Encodes [input] using [encoder], and puts it into an [NbtCompound].
 *
 * @param T The type of [input]
 * @param key The key in the receiver [NbtCompound] to associate [input] with
 * @param input The data to encode
 * @param encoder The codec used to encode [input]
 * @param ops The [DynamicOps] obtained from [RegistryWrapper.WrapperLookup.getOps]
 */
fun <T> NbtCompound.encode(key: String, input: T, encoder: Encoder<T>, ops: DynamicOps<NbtElement>) {
    put(key, encoder.encodeStart(ops, input).getOrThrow(false) { })
}

/**
 * Creates a new [NbtCompound] and puts it into the receiver [NbtCompound].
 *
 * @param key The key in the receiver [NbtCompound] to associate the new [NbtCompound] with
 * @return The created [NbtCompound]
 */
fun NbtCompound.subCompound(key: String) = NbtCompound().also { put(key, it) }
