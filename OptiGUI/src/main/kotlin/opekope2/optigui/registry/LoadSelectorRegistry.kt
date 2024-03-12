package opekope2.optigui.registry

/**
 * Registry holding registered load selectors.
 *
 * Load selectors decide which resources should be loaded as filters, when the resources are (re)loaded (when the game
 * starts, the user changes resource packs, or presses F3+T).
 *
 * Each load selector accepts the string representation of a selector, parses and evaluates it, and returns a boolean
 * indicating if the resource should be loaded.
 */
object LoadSelectorRegistry : Iterable<Pair<String, (String) -> Boolean>> {
    private val loadSelectors = mutableMapOf<String, (String) -> Boolean>()

    /**
     * Tries to register the given load selector with the given key.
     *
     * @param selectorKey The key (as in key-value pair) to register a load selector for
     * @param selector The load selector instance
     * @return `true` if the registration was successful, `false` if there was a selector or load selector registered
     *   for the given key
     */
    fun tryRegister(selectorKey: String, selector: (String) -> Boolean): Boolean {
        if (selectorKey in this || selectorKey in SelectorRegistry) return false

        loadSelectors[selectorKey] = selector
        return true
    }

    /**
     * Registers the given load selector with the given key or throws an exception, if there was a selector or load
     * selector registered for the given key.
     *
     * @param selectorKey The key (as in key-value pair) to register a load selector for
     * @param selector The load selector instance
     */
    fun register(selectorKey: String, selector: (String) -> Boolean) {
        if (!tryRegister(selectorKey, selector)) {
            throw IllegalStateException("A selector or load selector for `$selectorKey` has already been registered")
        }
    }

    /**
     * Checks if a load selector has been registered for the given key.
     *
     * @param selectorKey The key to check
     */
    operator fun contains(selectorKey: String): Boolean = selectorKey in loadSelectors

    /**
     * Gets the load selector instance associated with the given key.
     *
     * @param selectorKey The key to get a load selector associated with
     * @return The load selector associated with the given key, or `null`, if there was no load selector associated with it
     */
    operator fun get(selectorKey: String): ((String) -> Boolean)? = loadSelectors[selectorKey]

    override fun iterator(): Iterator<Pair<String, (String) -> Boolean>> =
        object : Iterator<Pair<String, (String) -> Boolean>> {
            private val iterator = loadSelectors.iterator()

            override fun hasNext(): Boolean = iterator.hasNext()

            override fun next(): Pair<String, (String) -> Boolean> {
                val (key, value) = iterator.next()
                return key to value
            }
        }
}
