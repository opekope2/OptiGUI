package opekope2.linker

import java.lang.invoke.MethodHandle

/**
 * An abstract linker class which lets you access classes and its public members at runtime
 * @param className The name of the class to access
 */
abstract class DynamicLinker(val className: String) {
    /**
     * Returns the handle to the requested method or null if not found.
     * If used from kotlin, use java primitive types (like `int` instead of [java.lang.Integer]) defined in [JavaPrimitive]
     *
     * @param methodName The name of the method
     * @param returnType The return type of the method
     * @param params The parameters of the method
     */
    abstract fun linkVirtualMethod(methodName: String, returnType: Class<*>, vararg params: Class<*>): MethodHandle?
}
