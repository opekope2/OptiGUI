package opekope2.optigui.util.registry

/**
 * OptiGUI base registration utility.
 * This is not to be confused with Minecraft registries.
 */
abstract class RegistryBase<K, V> : Iterable<Map.Entry<K, V>> {
    private val entries = mutableMapOf<K, V>()

    /**
     * Validates an entry to be registered. Throws an exception, if the entry is invalid.
     *
     * Implementors must call the super method to check if the key is not already registered.
     */
    open fun validateEntry(key: K, value: V) {
        require(key !in entries) { "Key is already registered: $key" }
    }

    /**
     * Registers an entry to this registry.
     * This method is thread-safe.
     *
     * @param key The key to associate a value with
     * @param value The value to register
     */
    open fun register(key: K, value: V) {
        synchronized(entries) {
            validateEntry(key, value)
            entries[key] = value
        }
    }

    /**
     * Checks if the given key is present in the registry
     *
     * @param key The key to check
     */
    operator fun contains(key: K) = key in entries

    /**
     * Gets the value associated with the given key or throws an exception, if the key is not present in this registry.
     *
     * @param key The key to check
     */
    fun getValue(key: K) = entries.getValue(key)

    override fun iterator(): Iterator<Map.Entry<K, V>> = entries.iterator()
}
