package opekope2.optigui.resource

/**
 * Registry holding OptiGUI resource loaders.
 */
object ResourceLoaders : Iterable<IResourceLoader<*>> {
    private val resourceLoaders = mutableSetOf<IResourceLoader<*>>()

    /**
     * Registers a resource loader.
     *
     * @param resourceLoader The resource loader instance
     */
    @JvmStatic
    fun register(resourceLoader: IResourceLoader<*>) {
        resourceLoaders += resourceLoader
    }

    override fun iterator(): Iterator<IResourceLoader<*>> = resourceLoaders.iterator()
}
