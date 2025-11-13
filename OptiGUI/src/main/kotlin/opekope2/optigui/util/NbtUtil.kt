@file:JvmName("NbtUtil")

package opekope2.optigui.util

import com.mojang.serialization.Encoder
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag

/**
 * Encodes [input] using [encoder] as NBT.
 *
 * @param T The type of [input]
 * @param input The data to encode
 * @param encoder The codec used to encode [input]
 * @param lookup The registry lookup to get [NbtOps] from
 */
fun <T> encode(input: T, encoder: Encoder<T>, lookup: HolderLookup.Provider): Tag =
    encoder.encodeStart(lookup.createSerializationContext(NbtOps.INSTANCE), input).getOrThrow()
