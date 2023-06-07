package opekope2.optigui.internal.service

import opekope2.optigui.resource.OptiGuiResource

interface ResourceLoaderService {
    fun loadResources(resources: Set<OptiGuiResource>)
}
