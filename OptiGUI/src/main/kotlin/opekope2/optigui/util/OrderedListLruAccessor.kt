package opekope2.optigui.util

import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap
import java.util.function.IntSupplier

/**
 * An optimized ordered list element accessor for the first match of a predicate.
 *
 * @param T The type of the list elements
 * @param list The reference list to copy elements from
 */
class OrderedListLruAccessor<T : OrderedListLruAccessor.ValueSupplier>(list: List<T>) {
    private val valueToListMap: Int2ObjectSortedMap<Link<T>> = Int2ObjectAVLTreeMap(Comparator.reverseOrder())

    init {
        for (element in list.asReversed()) {
            val elementValue = element.asInt
            val prevFirstLink = valueToListMap[elementValue]
            val link = Link(null, prevFirstLink, element)

            valueToListMap[elementValue] = link
            prevFirstLink?.prev = link
        }
    }

    /**
     * Gets the first element matching [predicate] and promotes it to the first place among the elements having the same value.
     * Returns `null` if no elements match [predicate].
     *
     * @param predicate
     */
    fun promoteFirstOrNull(predicate: (T) -> Boolean): T? {
        for ((value, head) in valueToListMap.int2ObjectEntrySet()) {
            var link = head

            while (link != null) {
                if (predicate(link.element)) {
                    promote(value, head, link)
                    return link.element
                }
                link = link.next
            }
        }

        return null
    }

    private fun promote(value: Int, head: Link<T>, link: Link<T>) {
        val prev = link.prev ?: return // already first
        val next = link.next

        // link prev and next together
        prev.next = next
        next?.prev = prev

        // insert before head
        link.prev = null
        link.next = head
        head.prev = link

        // update head
        valueToListMap[value] = link
    }

    private class Link<T>(var prev: Link<T>?, var next: Link<T>?, val element: T)

    /**
     * An element supplying its own value.
     */
    fun interface ValueSupplier : IntSupplier
}
