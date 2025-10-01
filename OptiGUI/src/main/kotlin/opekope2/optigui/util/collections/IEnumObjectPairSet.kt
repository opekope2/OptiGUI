package opekope2.optigui.util.collections

import java.util.function.BiConsumer
import java.util.function.Consumer

/**
 * A set of a [Pair] of an enum and an object that allows querying without allocating a [Pair] object.
 *
 * @param TFirst The type of [Pair.first]
 * @param TSecond The type of [Pair.second]
 */
interface IEnumObjectPairSet<TFirst : Enum<TFirst>, TSecond : Any> : Set<Pair<TFirst, TSecond>> {
    /**
     * @see Set.contains
     */
    fun contains(left: TFirst, right: TSecond): Boolean

    override fun contains(element: Pair<TFirst, TSecond>) = contains(element.first, element.second)

    /**
     * @see Set.forEach
     */
    fun forEach(action: BiConsumer<in TFirst, in TSecond>) {
        for ((left, right) in this) action.accept(left, right)
    }

    override fun forEach(action: Consumer<in Pair<TFirst, TSecond>>) {
        forEach { left, right ->
            action.accept(left to right)
        }
    }
}
