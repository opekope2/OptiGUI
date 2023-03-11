package opekope2.optigui.internal.interaction

import opekope2.filter.FilterInfo
import opekope2.optigui.resource.Resource

internal val filterFactories = mutableMapOf<String, MutableSet<(Resource) -> FilterInfo?>>()
