@file:JvmName("DfuUtil")

package opekope2.optigui.util.dfu

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import java.util.*
import java.util.function.Function

/**
 * Converts a `Codec<List<T>>` to `Codec<Set<T>>`.
 */
fun <T> Codec<List<T>>.toSet(): Codec<Set<T>> = xmap(List<T>::toSet, Set<T>::toList)

/**
 * Shortcut for `fieldOf(name).forGetter(getter)`.
 *
 * @param TClass The type of the record containing the field
 * @param TField The type of the field in the record
 * @param name The name of the field in the encoded representation
 * @param getter The function used to get the field's value from an object instance
 * @see Codec.fieldOf
 * @see MapCodec.forGetter
 */
fun <TClass, TField> Codec<TField>.field(
    name: String,
    getter: Function<TClass, TField>
): RecordCodecBuilder<TClass, TField> = fieldOf(name).forGetter(getter)

/**
 * Shortcut for `optionalFieldOf(name).forGetter(getter)`.
 *
 * @param TClass The type of the record containing the field
 * @param TField The type of the field in the record
 * @param name The name of the field in the encoded representation
 * @param getter The function used to get the field's value from an object instance
 * @see Codec.optionalFieldOf
 * @see MapCodec.forGetter
 */
fun <TClass, TField> Codec<TField>.optionalField(
    name: String,
    getter: Function<TClass, Optional<TField>>
): RecordCodecBuilder<TClass, Optional<TField>> = optionalFieldOf(name).forGetter(getter)

/**
 * Shortcut for `optionalFieldOf(name, default).forGetter(getter)`.
 *
 * @param TClass The type of the record containing the field
 * @param TField The type of the field in the record
 * @param name The name of the field in the encoded representation
 * @param getter The function used to get the field's value from an object instance
 * @param default The default value of the field if it's not present
 * @see Codec.optionalFieldOf
 * @see MapCodec.forGetter
 */
fun <TClass, TField> Codec<TField>.optionalField(
    name: String,
    getter: Function<TClass, TField>,
    default: TField
): RecordCodecBuilder<TClass, TField> = optionalFieldOf(name, default).forGetter(getter)

/**
 * Creates a [DelimitedListCodec] for the given codec.
 *
 * @see DelimitedListCodec
 */
fun <T> Codec<T>.delimitedListOf(vararg delimiters: Char): Codec<List<T>> = DelimitedListCodec<T>(delimiters, listOf())
