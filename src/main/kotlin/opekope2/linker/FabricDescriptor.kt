package opekope2.linker

import net.fabricmc.loader.api.FabricLoader

/**
 * Helper class to obtain field and method descriptors. Used by [FabricDynamicLinker].
 */
object FabricDescriptor {
    @JvmStatic
    private val mappingResolver = FabricLoader.getInstance().mappingResolver

    /**
     * Gets the descriptor of a method with the given parameters and return type.
     *
     * @param returnType The return type of the method
     * @param params The parameter types of the method (if any)
     * @see JvmPrimitive
     */
    @JvmStatic
    fun ofMethod(returnType: Class<*>, vararg params: Class<*>): String {
        val builder = StringBuilder("(")

        params.forEach { builder.append(ofClass(it)) }

        builder.append(")", ofClass(returnType))
        return builder.toString()
    }

    /**
     * Gets the descriptor of the given class.
     *
     * @param clazz The class to describe
     */
    @JvmStatic
    fun ofClass(clazz: Class<*>): String =
        Descriptor.ofClass(mappingResolver.unmapClassName("intermediary", clazz.name))
}
