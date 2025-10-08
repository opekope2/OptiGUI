package opekope2.optigui.util.collections

import java.util.function.BiPredicate
import java.util.function.Predicate

/**
 * A mutable set of a [Pair] of an enum and an object that allows query and modification without allocating a [Pair]
 * object.
 */
interface IEnumObjectPairMutableSet<TFirst : Enum<TFirst>, TSecond : Any> :
    IEnumObjectPairSet<TFirst, TSecond>, MutableSet<Pair<TFirst, TSecond>> {
    /**
     * @see MutableSet.add
     */
    fun add(left: TFirst, right: TSecond): Boolean

    override fun add(element: Pair<TFirst, TSecond>) = add(element.first, element.second)

    /**
     * @see MutableSet.remove
     */
    fun remove(left: TFirst, right: TSecond): Boolean

    override fun remove(element: Pair<TFirst, TSecond>) = remove(element.first, element.second)

    /**
     * @see MutableSet.removeIf
     */
    fun removeIf(filter: BiPredicate<in TFirst, in TSecond>): Boolean {
        var removed = false
        val itr = iterator()

        for ((left, right) in itr) {
            if (!filter.test(left, right)) continue

            itr.remove()
            removed = true
        }

        return removed
    }

    override fun removeIf(filter: Predicate<in Pair<TFirst, TSecond>>) = removeIf { left, right ->
        filter.test(left to right)
    }
}
