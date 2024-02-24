package opekope2.optigui.internal.interaction

import opekope2.optigui.filter.factory.FilterFactory

internal object FilterFactoryStore {
    val filterFactories = mutableMapOf<String, MutableSet<FilterFactory>>()

    fun add(modId: String, factory: FilterFactory) {
        if (modId !in filterFactories) {
            filterFactories[modId] = mutableSetOf()
        }
        filterFactories[modId]!!.add(factory)
    }
}
