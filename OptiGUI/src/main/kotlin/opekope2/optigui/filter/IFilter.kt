package opekope2.optigui.filter

import net.minecraft.nbt.NbtElement
import java.util.function.Predicate

/**
 * A filter filtering [NbtElement]s.
 */
typealias INbtFilter = IFilter<NbtElement?>

/**
 * Functional interface for filtering.
 *
 * @param T The type the filter accepts
 */
fun interface IFilter<T> : Predicate<T>
