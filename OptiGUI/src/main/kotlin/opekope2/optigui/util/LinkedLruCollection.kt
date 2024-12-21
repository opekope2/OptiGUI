package opekope2.optigui.util

import java.util.function.Predicate

/**
 * A read-only collection, which stores elements in LRU order.
 *
 * @param T The type of the elements in the collection
 * @param U The type of the parameter passed to [Predicate.test] on the collection elements
 * @param collection The elements of the collection
 */
class LinkedLruCollection<T : Predicate<U>, U>(collection: Collection<T>) : Collection<T> {
    private var head: Link<T>? = null

    override val size = collection.size

    init {
        var tail: Link<T>? = null
        for (element in collection) {
            val link = Link(tail, null, element)
            tail?.next = link

            tail = link
            if (head == null) head = link
        }
    }

    /**
     * Gets the first element, where [Predicate.test] returns `true`, and moves it to the first index.
     *
     * @param u The argument passed to [Predicate.test]
     * @return The first element where [Predicate.test] returns `true` or `null`, if every element returns `false`
     */
    fun promoteFirstOrNull(u: U): T? {
        var link = head

        while (link != null) {
            if (link.element.test(u)) {
                promote(link)
                return link.element
            }
            link = link.next
        }

        return null
    }

    private fun promote(link: Link<T>) {
        val prev = link.prev ?: return // already first
        val next = link.next

        // link prev and next together
        prev.next = next
        next?.prev = prev

        // insert before head
        link.prev = null
        link.next = head
        head!!.prev = link

        // update head
        head = link
    }

    override fun isEmpty() = head == null

    override fun iterator() = iterator {
        var link = head

        while (link != null) {
            yield(link.element)
            link = link.next
        }
    }

    override fun contains(element: T): Boolean {
        var link = head

        while (link != null) {
            if (link.element == element) return true
            link = link.next
        }

        return false
    }

    override fun containsAll(elements: Collection<T>) = elements.all(::contains)

    private class Link<T>(var prev: Link<T>?, var next: Link<T>?, val element: T)
}
