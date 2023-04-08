package opekope2.optigui.internal.interaction

import opekope2.filter.FilterInfo
import opekope2.optigui.resource.OptiGuiResource

internal val filterFactories = mutableMapOf<String, MutableSet<(OptiGuiResource) -> FilterInfo?>>()
