@file:JvmName("DfuUtil")

package opekope2.optigui.util.dfu

import com.mojang.datafixers.util.Function3
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.Lifecycle
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Supplier

/**
 * @see DataResult.success
 */
fun <T> success(value: T): DataResult<T> = DataResult.success(value)

/**
 * @see DataResult.success
 */
fun <T> success(value: T, lifecycle: Lifecycle): DataResult<T> = DataResult.success(value, lifecycle)

/**
 * @see DataResult.error
 */
fun <T> error(message: Supplier<String>): DataResult<T> = DataResult.error(message)

/**
 * @see DataResult.error
 */
fun <T> error(lifecycle: Lifecycle, message: Supplier<String>): DataResult<T> = DataResult.error(message, lifecycle)

/**
 * @see DataResult.error
 */
fun <T> error(partial: T, message: Supplier<String>): DataResult<T> = DataResult.error(message, partial)

/**
 * @see DataResult.error
 */
fun <T> error(partial: T, lifecycle: Lifecycle, message: Supplier<String>): DataResult<T> =
    DataResult.error(message, partial, lifecycle)

/**
 * @see DataResult.apply2
 */
fun <T1, T2, R> DataResult<T1>.apply2(second: DataResult<T2>, function: BiFunction<T1, T2, R>): DataResult<R> =
    apply2(function, second)

/**
 * @see DataResult.apply2stable
 */
fun <T1, T2, R> DataResult<T1>.apply2stable(second: DataResult<T2>, function: BiFunction<T1, T2, R>): DataResult<R> =
    apply2stable(function, second)

/**
 * @see DataResult.apply3
 */
fun <T1, T2, T3, R> DataResult<T1>.apply3(
    second: DataResult<T2>,
    third: DataResult<T3>,
    function: Function3<T1, T2, T3, R>
): DataResult<R> = apply3(function, second, third)

/**
 * Converts a `Codec<List<T>>` to `Codec<Set<T>>`.
 */
fun <T> Codec<List<T>>.toSet(): Codec<Set<T>> = xmap(List<T>::toSet, Set<T>::toList)

/**
 * Shortcut for `fieldOf(name).forGetter(getter)`.
 *
 * @param S The type of the record containing the field
 * @param T The type of the field in the record
 * @param name The name of the field in the encoded representation
 * @param getter The function used to get the field's value from an object instance
 * @see Codec.fieldOf
 * @see MapCodec.forGetter
 */
fun <S, T> Codec<T>.field(
    name: String,
    getter: Function<S, T>
): RecordCodecBuilder<S, T> = fieldOf(name).forGetter(getter)

/**
 * Shortcut for `optionalFieldOf(name).forGetter(getter)`.
 *
 * @param S The type of the record containing the field
 * @param T The type of the field in the record
 * @param name The name of the field in the encoded representation
 * @param getter The function used to get the field's value from an object instance
 * @see Codec.optionalFieldOf
 * @see MapCodec.forGetter
 */
fun <S, T> Codec<T>.optionalField(
    name: String,
    getter: Function<S, Optional<T>>
): RecordCodecBuilder<S, Optional<T>> = optionalFieldOf(name).forGetter(getter)

/**
 * Shortcut for `optionalFieldOf(name, default).forGetter(getter)`.
 *
 * @param S The type of the record containing the field
 * @param T The type of the field in the record
 * @param name The name of the field in the encoded representation
 * @param getter The function used to get the field's value from an object instance
 * @param default The default value of the field if it's not present
 * @see Codec.optionalFieldOf
 * @see MapCodec.forGetter
 */
fun <S, T> Codec<T>.optionalField(
    name: String,
    getter: Function<S, T>,
    default: T
): RecordCodecBuilder<S, T> = optionalFieldOf(name, default).forGetter(getter)

/**
 * Creates a [DelimitedListCodec] for the given codec.
 *
 * @see DelimitedListCodec
 */
fun <T> Codec<T>.delimitedListOf(vararg delimiters: Char): Codec<List<T>> = DelimitedListCodec<T>(delimiters, listOf())
