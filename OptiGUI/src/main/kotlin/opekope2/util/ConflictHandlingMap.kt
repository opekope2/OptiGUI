package opekope2.util

import opekope2.lilac.util.Tree


internal class ConflictHandlingMap<TKey : Any, TValue : IIdentifiable> : MutableMap<TKey, TValue> {
    private val map = mutableMapOf<TKey, MutableList<TValue>>()

    override val size: Int
        get() = map.size

    override val keys: MutableSet<TKey>
        get() = map.keys

    override val values: MutableCollection<TValue>
        get() {
            ensureNoConflicts()
            return map.mapTo(mutableListOf()) { (_, values) -> values.first() }
        }

    override val entries: MutableSet<MutableMap.MutableEntry<TKey, TValue>>
        get() {
            ensureNoConflicts()
            return map.mapTo(mutableSetOf()) { (key, values) -> Entry(key, values.first()) }
        }

    var conflicts = false
        private set

    @Suppress("NOTHING_TO_INLINE")
    private inline fun ensureNoConflicts() {
        if (conflicts) {
            throw IllegalStateException("The map has conflicting entries")
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun recalculateConflicts() {
        conflicts = map.any { (_, values) -> values.size > 1 }
    }

    override fun containsKey(key: TKey): Boolean = key in map

    override fun containsValue(value: TValue): Boolean = map.any { (_, v) -> value in v }

    override fun clear() {
        map.clear()
        conflicts = false
    }

    override fun get(key: TKey): TValue? {
        ensureNoConflicts()
        return map[key]?.first()
    }

    override fun isEmpty(): Boolean = map.isEmpty()

    override fun put(key: TKey, value: TValue): TValue? {
        if (key in map) {
            conflicts = true
        }

        map.getOrPut(key) { mutableListOf() }.add(value)
        return null
    }

    override fun putIfAbsent(key: TKey, value: TValue): TValue? {
        if (key !in map) {
            put(key, value)
        }
        return null
    }

    override fun putAll(from: Map<out TKey, TValue>) = from.forEach { (key, value) -> put(key, value) }

    override fun remove(key: TKey): TValue? {
        map.remove(key)
        recalculateConflicts()
        return null
    }

    override fun remove(key: TKey, value: TValue): Boolean {
        if (map[key]?.any { it == value } == true) {
            map[key]!!.remove(value)
            recalculateConflicts()
            return true
        }
        return false
    }

    override fun replace(key: TKey, value: TValue): TValue? {
        if (key in map) {
            remove(key)
            put(key, value)
        }
        return null
    }

    override fun replace(key: TKey, oldValue: TValue, newValue: TValue): Boolean {
        if (remove(key, oldValue)) {
            put(key, newValue)
            return true
        }
        return false
    }

    fun createConflictTree(rootText: String): Tree.Node {
        val root = Tree.Node(rootText)

        for ((key, values) in map) {
            if (values.size <= 1) continue

            root.appendChild(key.toString()).apply {
                for (value in values) {
                    appendChild("$value (${value.id})")
                }
            }
        }

        return root
    }

    private class Entry<TKey, TValue>(override val key: TKey, value: TValue) : MutableMap.MutableEntry<TKey, TValue> {
        override var value: TValue = value
            private set

        override fun setValue(newValue: TValue): TValue {
            val oldValue = value
            value = newValue
            return oldValue
        }
    }
}
