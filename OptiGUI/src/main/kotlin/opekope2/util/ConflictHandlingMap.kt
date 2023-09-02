package opekope2.util

import opekope2.lilac.util.Tree

internal class ConflictHandlingMap<TKey, TValue> : Map<TKey, TValue> {
    private val map = mutableMapOf<TKey, MutableList<Pair<String, TValue>>>()

    override val size: Int
        get() = map.size

    override val keys: Set<TKey>
        get() = map.keys

    override val values: Collection<TValue>
        get() {
            ensureNoConflicts()
            return map.map { (_, values) -> values.first().second }
        }

    override val entries: Set<Map.Entry<TKey, TValue>>
        get() {
            ensureNoConflicts()
            return map.mapTo(mutableSetOf()) { (key, values) -> Entry(key, values.first().second) }
        }

    var conflicts = false
        private set

    @Suppress("NOTHING_TO_INLINE")
    private inline fun ensureNoConflicts() {
        if (conflicts) {
            throw IllegalStateException("The map has conflicting entries")
        }
    }

    override fun containsKey(key: TKey): Boolean = key in map

    override fun containsValue(value: TValue): Boolean =
        map.any { (_, values) -> values.any { (_, v) -> v == value } }

    override fun get(key: TKey): TValue? {
        ensureNoConflicts()
        return map[key]?.first()?.second
    }

    override fun isEmpty(): Boolean = map.isEmpty()

    fun put(key: TKey, modId: String, value: TValue) {
        if (key in map) {
            conflicts = true
        }

        map.getOrPut(key) { mutableListOf() }.add(modId to value)
    }

    fun createConflictTree(rootText: String): Tree.Node {
        val root = Tree.Node(rootText)

        for ((key, value) in map) {
            if (value.size <= 1) continue

            root.appendChild(key.toString()).apply {
                for ((k, v) in value) {
                    appendChild("$v ($k)")
                }
            }
        }

        return root
    }

    private class Entry<TKey, TValue>(override val key: TKey, override val value: TValue) : Map.Entry<TKey, TValue>
}
