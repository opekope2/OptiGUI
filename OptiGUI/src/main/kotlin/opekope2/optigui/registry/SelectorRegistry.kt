package opekope2.optigui.registry

import opekope2.optigui.selector.ISelector

/**
 * Registry holding registered selectors.
 */
object SelectorRegistry : Iterable<Pair<String, ISelector>> {
    private val selectors = mutableMapOf<String, ISelector>()

    /**
     * Tries to register the given selector with the given key.
     *
     * @param selectorKey The key (as in key-value pair) to register a selector for
     * @param selector The selector instance
     * @return `true` if the registration was successful, `false` if there was a selector or load-time selector registered
     *   for the given key
     */
    fun tryRegister(selectorKey: String, selector: ISelector): Boolean {
        if (selectorKey in this || selectorKey in LoadTimeSelectorRegistry) return false

        selectors[selectorKey] = selector
        return true
    }

    /**
     * Registers the given selector with the given key or throws an exception, if there was a selector or load-time
     * selector registered for the given key.
     *
     * @param selectorKey The key (as in key-value pair) to register a selector for
     * @param selector The selector instance
     */
    fun register(selectorKey: String, selector: ISelector) {
        if (!tryRegister(selectorKey, selector)) {
            throw IllegalStateException("A selector or load-time selector for `$selectorKey` has already been registered")
        }
    }

    /**
     * Checks if a selector has been registered for the given key.
     *
     * @param selectorKey The key to check
     */
    operator fun contains(selectorKey: String): Boolean = selectorKey in selectors

    /**
     * Gets the selector instance associated with the given key.
     *
     * @param selectorKey The key to get a selector associated with
     * @return The selector associated with the given key, or `null`, if there was no selector associated with it
     */
    operator fun get(selectorKey: String): ISelector? = selectors[selectorKey]

    override fun iterator(): Iterator<Pair<String, ISelector>> = object : Iterator<Pair<String, ISelector>> {
        private val iterator = selectors.iterator()

        override fun hasNext(): Boolean = iterator.hasNext()

        override fun next(): Pair<String, ISelector> {
            val (key, value) = iterator.next()
            return key to value
        }
    }
}
