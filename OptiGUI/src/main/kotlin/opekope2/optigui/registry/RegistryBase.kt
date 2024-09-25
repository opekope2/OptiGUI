package opekope2.optigui.registry

/**
 * OptiGUI base registration utility.
 * This is not to be confused with Minecraft registries.
 */
abstract class RegistryBase<TKey, TValue> : Iterable<Map.Entry<TKey, TValue>> {
    private val entries = mutableMapOf<TKey, TValue>()

    /**
     * Validates an entry to be registered. Throws an exception, if the entry is invalid.
     *
     * Implementors must call the super method to check if the key is not already registered.
     */
    open fun validateEntry(key: TKey, value: TValue) {
        require(key !in entries) { "Key `$key` is already registered" }
    }

    /**
     * Registers an entry to this registry.
     *
     * @param key The key to associate a value with
     * @param value The value to register
     */
    open fun register(key: TKey, value: TValue) {
        validateEntry(key, value)
        entries[key] = value
    }

    /**
     * Checks if the given key is present in the registry
     *
     * @param key The key to check
     */
    operator fun contains(key: TKey) = key in entries

    /**
     * Gets the value associated with the given key or throws an exception, if the key is not present in this registry.
     *
     * @param key The key to check
     */
    fun getValue(key: TKey) = entries.getValue(key)

    override fun iterator(): Iterator<Map.Entry<TKey, TValue>> = entries.iterator()
}
