package opekope2.util

import opekope2.lilac.util.Tree

internal class ConflictHandlingMap<TKey, TValue> {
    private val map = mutableMapOf<TKey, MutableList<Pair<String, TValue>>>()

    var conflicts = false
        private set

    private fun ensureNoConflicts() {
        if (conflicts) {
            throw IllegalStateException()
        }
    }

    fun put(key: TKey, modId: String, value: TValue) {
        if (key in map) {
            conflicts = true
        }

        map.getOrPut(key) { mutableListOf() }.add(modId to value)
    }

    fun get(key: TKey): TValue? {
        ensureNoConflicts()
        return map[key]?.first()?.second
    }

    fun toMap(): Map<TKey, TValue> {
        ensureNoConflicts()
        return map.mapValues { (_, value) -> value.first().second }
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
}
