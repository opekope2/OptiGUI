package opekope2.optigui.util.collections

import java.util.*
import java.util.function.BiConsumer
import java.util.function.BiPredicate

/**
 * Default implementation of [IEnumObjectPairMutableSet].
 *
 * @param TFirst The type of [Pair.first]
 * @param TSecond The type of [Pair.second]
 * @param enumClass The class of [TFirst]
 */
class EnumObjectPairMutableSet<TFirst : Enum<TFirst>, TSecond : Any>(enumClass: Class<TFirst>) :
    IEnumObjectPairMutableSet<TFirst, TSecond> {
    init {
        require(enumClass.enumConstants.isNotEmpty()) { "$enumClass has no enum values" }
    }

    private val set: EnumMap<TFirst, MutableSet<TSecond>> =
        EnumMap(enumClass.enumConstants.associateWith { mutableSetOf() })

    override fun iterator() = object : MutableIterator<Pair<TFirst, TSecond>> {
        private var firstIterator = set.keys.iterator()
        private lateinit var secondIterator: MutableIterator<TSecond>
        private lateinit var first: TFirst
        private var hasNext = advanceSecondIterator()

        override fun remove() {
            secondIterator.remove()
        }

        override fun next(): Pair<TFirst, TSecond> {
            if (!hasNext) throw NoSuchElementException()
            if (!secondIterator.hasNext()) throw NoSuchElementException()

            val value = secondIterator.next()
            hasNext = advanceSecondIterator()
            return first to value
        }

        private tailrec fun advanceSecondIterator(): Boolean {
            if (::secondIterator.isInitialized && secondIterator.hasNext()) return true
            if (!firstIterator.hasNext()) return false

            first = firstIterator.next()
            secondIterator = set[first]!!.iterator()
            return if (secondIterator.hasNext()) true
            else advanceSecondIterator()
        }

        override fun hasNext() = hasNext
    }

    override fun add(left: TFirst, right: TSecond) = set[left]!!.add(right)

    override fun remove(left: TFirst, right: TSecond) = set[left]!!.remove(right)

    // Override so no iterator and pairs get allocated
    override fun removeIf(filter: BiPredicate<in TFirst, in TSecond>): Boolean {
        var removed = false

        for ((left, rights) in set) {
            for (right in rights) {
                if (!filter.test(left, right)) continue

                remove(left, right)
                removed = true
            }
        }

        return removed
    }

    override fun addAll(elements: Collection<Pair<TFirst, TSecond>>) = elements.any(::add)

    override fun removeAll(elements: Collection<Pair<TFirst, TSecond>>) = elements.any(::remove)

    override fun retainAll(elements: Collection<Pair<TFirst, TSecond>>): Boolean {
        var modified = false
        val itr = iterator()

        for (element in itr) {
            if (element in elements) continue

            itr.remove()
            modified = true
        }

        return modified
    }

    override fun clear() {
        set.values.forEach(MutableSet<*>::clear)
    }

    override val size: Int
        get() = set.values.fold(0) { acc, values -> acc + values.size }

    override fun isEmpty() = set.all { it.value.isEmpty() }

    override fun contains(left: TFirst, right: TSecond) = right in set[left]!!

    override fun forEach(action: BiConsumer<in TFirst, in TSecond>) {
        for ((left, rights) in set) {
            for (right in rights) action.accept(left, right)
        }
    }

    override fun containsAll(elements: Collection<Pair<TFirst, TSecond>>) = elements.all { it in this }

    /**
     * Unmodifiable view of [EnumObjectPairMutableSet].
     */
    inner class View : IEnumObjectPairSet<TFirst, TSecond> by this
}
