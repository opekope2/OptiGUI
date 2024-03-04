package opekope2.optigui.registry

import opekope2.optigui.selector.ILoadTimeSelector

/**
 * Registry holding registered [load-time selectors][ILoadTimeSelector].
 */
object LoadTimeSelectorRegistry : Iterable<Pair<String, ILoadTimeSelector>> {
    private val loadTimeSelectors = mutableMapOf<String, ILoadTimeSelector>()

    /**
     * Tries to register the given load-time selector with the given key.
     *
     * @param selectorKey The key (as in key-value pair) to register a load-time selector for
     * @param selector The load-time selector instance
     * @return `true` if the registration was successful, `false` if there was a selector or load-time selector registered
     *   for the given key
     */
    fun tryRegister(selectorKey: String, selector: ILoadTimeSelector): Boolean {
        if (selectorKey in this || selectorKey in SelectorRegistry) return false

        loadTimeSelectors[selectorKey] = selector
        return true
    }

    /**
     * Registers the given load-time selector with the given key or throws an exception, if there was a selector or
     * load-time selector registered for the given key.
     *
     * @param selectorKey The key (as in key-value pair) to register a load-time selector for
     * @param selector The load-time selector instance
     */
    fun register(selectorKey: String, selector: ILoadTimeSelector) {
        if (!tryRegister(selectorKey, selector)) {
            throw IllegalStateException("A selector or load-time selector for `$selectorKey` has already been registered")
        }
    }

    /**
     * Checks if a load-time selector has been registered for the given key.
     *
     * @param selectorKey The key to check
     */
    operator fun contains(selectorKey: String): Boolean = selectorKey in loadTimeSelectors

    /**
     * Gets the load-time selector instance associated with the given key.
     *
     * @param selectorKey The key to get a load-time selector associated with
     * @return The load-time selector associated with the given key, or `null`, if there was no load-time selector
     *   associated with it
     */
    operator fun get(selectorKey: String): ILoadTimeSelector? = loadTimeSelectors[selectorKey]

    override fun iterator(): Iterator<Pair<String, ILoadTimeSelector>> =
        object : Iterator<Pair<String, ILoadTimeSelector>> {
            private val iterator = loadTimeSelectors.iterator()

            override fun hasNext(): Boolean = iterator.hasNext()

            override fun next(): Pair<String, ILoadTimeSelector> {
                val (key, value) = iterator.next()
                return key to value
            }
        }
}
