package opekope2.optigui.filter

import net.minecraft.nbt.NbtElement
import java.util.function.Predicate

/**
 * A filter filtering [NbtElement]s.
 */
typealias INbtFilter = IFilter<NbtElement?>

/**
 * A filter evaluated at load time.
 * Note that this doesn't take an input, because the implementor provides it.
 */
typealias ILoadTimeFilter = IFilter<Nothing?>

/**
 * Functional interface for filtering.
 *
 * @param T The type the filter accepts
 */
fun interface IFilter<T> : Predicate<T>
