package opekope2.optigui.internal.service

import opekope2.optigui.resource.ResourceReader

/**
 * A service for loading resources. Called by OptiGUI.
 */
interface ResourceLoaderService {
    /**
     * Loads and processes the given resources.
     *
     * @param resources The resources to load
     */
    fun loadResources(resources: Iterable<ResourceReader>)
}
