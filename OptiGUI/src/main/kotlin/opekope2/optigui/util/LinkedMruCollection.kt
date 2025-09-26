package opekope2.optigui.util

/**
 * A read-only collection, which stores elements in most recently used order.
 *
 * @param T The type of the elements in the collection
 * @param collection The elements of the collection
 */
class LinkedMruCollection<T>(collection: Collection<T>) : AbstractCollection<T>() {
    private var head: Link? = null

    override val size = collection.size

    init {
        var tail: Link? = null
        for (element in collection) {
            tail = Link(tail, element)
            if (head == null) head = tail
        }
    }

    /**
     * Finds and [promotes][IIterator.promote] the first element which matches [predicate].
     *
     * @return `true` if such element was found, `false` otherwise
     */
    inline fun promoteFirst(predicate: (T) -> Boolean): Boolean {
        val itr = iterator()

        for (elem in itr) {
            if (!predicate(elem)) continue

            itr.promote()
            return true
        }

        return false
    }

    /**
     * Gets and [promotes][IIterator.promote] the first element which matches [predicate].
     *
     * @return The element if found, `null` otherwise
     */
    inline fun promoteFirstOrNull(predicate: (T) -> Boolean): T? {
        val itr = iterator()

        for (elem in itr) {
            if (!predicate(elem)) continue

            itr.promote()
            return elem
        }

        return null
    }

    override fun isEmpty() = head == null

    override fun iterator() = object : IIterator<T> {
        private lateinit var current: Link
        private var next: Link? = head

        override fun hasNext() = next != null

        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException()
            current = next!!
            next = current.next
            return current.element
        }

        override fun promote() {
            current.promote()
        }
    }

    /**
     * An iterator over a [LinkedMruCollection]. Provides the ability to promote elements while iterating.
     *
     * @see iterator
     */
    interface IIterator<T> : Iterator<T> {
        /**
         * Moves the last element returned by this iterator to the head of the collection.
         * The iteration continues from the element previously after the promoted element.
         */
        fun promote()
    }

    private inner class Link(private var prev: Link?, val element: T) {
        var next: Link? = null
            private set

        init {
            assert(prev?.next == null) { "Previous link must not have a subsequent link" }
            prev?.next = this
        }

        fun promote() {
            val prev = this.prev ?: return // already first
            val next = this.next

            // link prev and next together
            prev.next = next
            next?.prev = prev

            // insert before head
            this.prev = null
            this.next = head
            head!!.prev = this

            // update head
            head = this
        }
    }
}
