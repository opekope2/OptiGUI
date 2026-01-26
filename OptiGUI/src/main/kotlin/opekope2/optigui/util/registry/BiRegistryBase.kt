package opekope2.optigui.util.registry

/**
 * A registry that has a value-to-key mapping in addition to the key-to-value mapping, which means that every value can
 * only be registered to one key.
 */
abstract class BiRegistryBase<K, V> : RegistryBase<K, V>() {
    private val reverseEntries = mutableMapOf<V, K>()

    override fun validateEntry(key: K, value: V) {
        super.validateEntry(key, value)
        require(value !in reverseEntries) { "Value is already registered: $value" }
    }

    override fun register(key: K, value: V) {
        super.register(key, value)
        reverseEntries[value] = key
    }

    /**
     * Checks if the given value is registered in this registry.
     *
     * @param value The value to check
     */
    fun containsValue(value: V) = value in reverseEntries

    /**
     * Gets the key associated with the given value or throws an exception, if the value is not present in this registry.
     *
     * @param value The value to check
     */
    fun getKey(value: V) = reverseEntries.getValue(value)
}
