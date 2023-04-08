package opekope2.optigui.internal.interaction

import opekope2.filter.FilterInfo
import opekope2.optigui.resource.OptiGuiResource

internal object FilterFactoryStore {
    val filterFactories = mutableMapOf<String, MutableSet<(OptiGuiResource) -> FilterInfo?>>()

    fun add(modId: String, factory: (OptiGuiResource) -> FilterInfo?) {
        if (modId !in filterFactories) {
            filterFactories[modId] = mutableSetOf()
        }
        filterFactories[modId]!!.add(factory)
    }
}
