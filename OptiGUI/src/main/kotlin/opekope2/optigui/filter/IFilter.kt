package opekope2.optigui.filter

import net.minecraft.nbt.NbtElement
import opekope2.optigui.interaction.Interaction
import java.util.function.Predicate

/**
 * A filter filtering [NbtElement]s.
 */
typealias INbtFilter = IFilter<NbtElement?>

/**
 * A filter filtering [Interaction]s.
 */
typealias IInteractionFilter = IFilter<Interaction>

/**
 * Functional interface for filtering.
 *
 * @param T The type the filter accepts
 */
fun interface IFilter<T> : Predicate<T>
