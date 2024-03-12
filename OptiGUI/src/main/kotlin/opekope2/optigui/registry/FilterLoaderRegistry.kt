package opekope2.optigui.registry

import opekope2.optigui.resource.IFilterLoader

/**
 * Registry holding filter loaders.
 */
object FilterLoaderRegistry : Iterable<IFilterLoader> {
    private val resourceLoaders = mutableSetOf<IFilterLoader>()

    /**
     * Registers a filter loader.
     *
     * @param resourceLoader The filter loader instance
     */
    @JvmStatic
    fun register(resourceLoader: IFilterLoader) {
        resourceLoaders += resourceLoader
    }

    override fun iterator(): Iterator<IFilterLoader> = resourceLoaders.iterator()
}
