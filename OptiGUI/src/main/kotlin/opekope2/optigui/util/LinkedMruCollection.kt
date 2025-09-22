package opekope2.optigui.util

/**
 * A read-only collection, which stores elements in most recently used order.
 *
 * @param T The type of the elements in the collection
 * @param collection The elements of the collection
 */
class LinkedMruCollection<T>(collection: Collection<T>) : AbstractCollection<T>() {
    /**
     * The most recently used element in the collection.
     */
    var head: Link? = null
        private set

    override val size = collection.size

    init {
        var tail: Link? = null
        for (element in collection) {
            tail = Link(tail, element)
            if (head == null) head = tail
        }
    }

    // Must be set to true after creating all Links
    private val initialized = true

    /**
     * Finds and [promotes][Link.promote] the first element which matches [predicate].
     *
     * @return `true` if such element was found, `false` otherwise
     */
    inline fun promoteFirst(predicate: (T) -> Boolean): Boolean {
        var link = head

        while (link != null) {
            if (predicate(link.element)) {
                link.promote()
                return true
            }
            link = link.next
        }

        return false
    }

    /**
     * Gets and [promotes][Link.promote] the first element which matches [predicate].
     *
     * @return The element if found, `null` otherwise
     */
    inline fun promoteFirstOrNull(predicate: (T) -> Boolean): T? {
        var link = head

        while (link != null) {
            if (predicate(link.element)) {
                link.promote()
                return link.element
            }
            link = link.next
        }

        return null
    }

    override fun isEmpty() = head == null

    override fun iterator() = iterator {
        var link = head

        while (link != null) {
            yield(link.element)
            link = link.next
        }
    }

    /**
     * A link of the [LinkedMruCollection].
     *
     * @param prev The previous link or `null`, if this is the first link
     * @param element A value stored in the collection
     */
    inner class Link internal constructor(private var prev: Link?, val element: T) {
        /**
         * The subsequent link or `null`, if this is the last link.
         */
        var next: Link? = null
            private set

        init {
            check(!initialized) { "Collection must not be initialized" }
            require(prev?.next == null) { "Previous link must not have a subsequent link" }
            prev?.next = this
        }

        /**
         * Moves this link to the [head] of the collection.
         */
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
