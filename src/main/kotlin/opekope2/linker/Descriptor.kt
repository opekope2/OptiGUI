package opekope2.linker

/**
 * Helper class to obtain field and method descriptors. Used by [FabricDynamicLinker].
 */
class Descriptor {
    companion object {
        private val primitiveDesc = mapOf(
            JvmPrimitive.BYTE to "B",
            JvmPrimitive.CHAR to "C",
            JvmPrimitive.DOUBLE to "D",
            JvmPrimitive.FLOAT to "F",
            JvmPrimitive.INT to "I",
            JvmPrimitive.LONG to "J",
            JvmPrimitive.SHORT to "S",
            JvmPrimitive.BOOLEAN to "Z",
            JvmPrimitive.VOID to "V"
        )

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
         */
        @JvmStatic
        fun ofClass(className: String): String = binaryName("L$className;")

        /**
         * Gets the descriptor of the given class.
         */
        @JvmStatic
        fun ofClass(clazz: Class<*>): String =
            primitiveDesc[clazz] ?: binaryName(if (clazz.isArray) clazz.name else "L${clazz.name};")

        /**
         * Gets the binary name of the given class.
         */
        @JvmStatic
        fun binaryName(className: String) = className.replace('.', '/')

        /**
         * Gets the binary name of the given class.
         */
        @JvmStatic
        fun binaryName(clazz: Class<*>): String = binaryName(clazz.name)
    }
}
