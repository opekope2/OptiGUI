package opekope2.linker

/**
 * Helper class to obtain field and method descriptors. Used by [FabricDynamicLinker]
 */
class Descriptor {
    companion object {
        private val primitiveDesc = mapOf(
            JavaPrimitive.Byte to "B",
            JavaPrimitive.Char to "C",
            JavaPrimitive.Double to "D",
            JavaPrimitive.Float to "F",
            JavaPrimitive.Int to "I",
            JavaPrimitive.Long to "J",
            JavaPrimitive.Short to "S",
            JavaPrimitive.Boolean to "Z",
            JavaPrimitive.Void to "V"
        )

        /**
         * Gets the descriptor of a method with the given parameters and return type
         *
         * @param returnType The return type of the method
         * @param params The parameter types of the method (if any)
         * @see JavaPrimitive
         */
        @JvmStatic
        fun ofMethod(returnType: Class<*>, vararg params: Class<*>): String {
            val builder = StringBuilder("(")

            params.forEach { builder.append(ofClass(it)) }

            builder.append(")${ofClass(returnType)}")
            return builder.toString()
        }

        /**
         * Gets the descriptor of the given class. This should be used when supplying the type of a field
         */
        @JvmStatic
        fun ofClass(clazz: Class<*>): String =
            primitiveDesc[clazz] ?: (if (clazz.isArray) clazz.name else "L${clazz.name};").replace('.', '/')
    }
}
