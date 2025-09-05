@file: JvmName("DfuUtil")

package opekope2.optigui.util.dfu

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import java.util.*
import kotlin.reflect.KProperty1

/**
 * Converts a `Codec<List<T>>` to `Codec<Set<T>>`.
 *
 * @see toEnumSet
 */
fun <T> Codec<List<T>>.toSet(): Codec<Set<T>> = xmap(List<T>::toSet, Set<T>::toList)

/**
 * Converts a `Codec<List<T>>` to `Codec<EnumSet<T>>`.
 *
 * @see toSet
 */
inline fun <reified T : Enum<T>> Codec<List<T>>.toEnumSet(): Codec<EnumSet<T>> =
    xmap({ if (it.isEmpty()) EnumSet.noneOf(T::class.java) else EnumSet.copyOf(it) }, EnumSet<T>::toList)

/**
 * Shortcut for `fieldOf(field.name).forGetter(field::get)`.
 *
 * @param TClass The type of the record containing the field
 * @param TField The type of the field in the record
 * @param field The property used to get the field's name and getter
 * @see Codec.fieldOf
 * @see MapCodec.forGetter
 */
fun <TClass, TField> Codec<TField>.field(field: KProperty1<TClass, TField>): RecordCodecBuilder<TClass, TField> =
    fieldOf(field.name).forGetter(field::get)

/**
 * Shortcut for `optionalFieldOf(field.name).forGetter(field::get)`.
 *
 * @param TClass The type of the record containing the field
 * @param TField The type of the field in the record
 * @param field The property used to get the field's name and getter
 * @see Codec.optionalFieldOf
 * @see MapCodec.forGetter
 */
fun <TClass, TField> Codec<TField>.optionalField(field: KProperty1<TClass, Optional<TField>>): RecordCodecBuilder<TClass, Optional<TField>> =
    optionalFieldOf(field.name).forGetter(field::get)

/**
 * Creates a [DelimitedListCodec] for the given codec.
 *
 * @see DelimitedListCodec
 */
fun <T> Codec<T>.delimitedListOf(vararg delimiters: Char): Codec<List<T>> = DelimitedListCodec<T>(delimiters, listOf())
