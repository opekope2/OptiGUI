package opekope2.optigui.internal.service

import opekope2.optigui.resource.Resource

interface ResourceLoaderService {
    fun loadResources(resources: Iterable<Resource>)
}
