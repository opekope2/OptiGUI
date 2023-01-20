package opekope2.linker

import java.lang.invoke.MethodHandle

/**
 * An abstract linker class which lets you access classes and its public members at runtime.
 * If the linking fails, the constructor should throw.
 *
 * @param className The name of the class to access
 */
abstract class DynamicLinker(val className: String) {
    /**
     * The linked class
     */
    abstract val clazz: Class<*>

    /**
     * Returns the handle to the requested constructor or `null` if not found.
     * If used from kotlin, use java primitive types (like `int` instead of [java.lang.Integer]) defined in [JvmPrimitive]
     *
     * @param params The parameters of the constructor
     */
    abstract fun findConstructor(vararg params: Class<*>): MethodHandle?

    /**
     * Returns the handle to the getter method of the requested field or `null` if not found.
     * If used from kotlin, use java primitive types (like `int` instead of [java.lang.Integer]) defined in [JvmPrimitive]
     *
     * @param fieldName The name of the field
     * @param fieldType The type of the field
     */
    abstract fun findGetter(fieldName: String, fieldType: Class<*>): MethodHandle?

    /**
     * Returns the handle to the setter method of the requested field or `null` if not found.
     * If used from kotlin, use java primitive types (like `int` instead of [java.lang.Integer]) defined in [JvmPrimitive]
     *
     * @param fieldName The name of the field
     * @param fieldType The type of the field
     */
    abstract fun findSetter(fieldName: String, fieldType: Class<*>): MethodHandle?

    /**
     * Returns the handle to the requested static method or `null` if not found.
     * If used from kotlin, use java primitive types (like `int` instead of [java.lang.Integer]) defined in [JvmPrimitive]
     *
     * @param methodName The name of the method
     * @param returnType The return type of the method
     * @param params The parameters of the method
     */
    abstract fun findStaticMethod(methodName: String, returnType: Class<*>, vararg params: Class<*>): MethodHandle?

    /**
     * Returns the handle to the getter method of the requested static field or `null` if not found.
     * If used from kotlin, use java primitive types (like `int` instead of [java.lang.Integer]) defined in [JvmPrimitive]
     *
     * @param fieldName The name of the static field
     * @param fieldType The type of the static field
     */
    abstract fun findStaticGetter(fieldName: String, fieldType: Class<*>): MethodHandle?

    /**
     * Returns the handle to the setter method of the requested static field or `null` if not found.
     * If used from kotlin, use java primitive types (like `int` instead of [java.lang.Integer]) defined in [JvmPrimitive]
     *
     * @param fieldName The name of the static field
     * @param fieldType The type of the static field
     */
    abstract fun findStaticSetter(fieldName: String, fieldType: Class<*>): MethodHandle?

    /**
     * Returns the handle to the requested method or `null` if not found.
     * If used from kotlin, use java primitive types (like `int` instead of [java.lang.Integer]) defined in [JvmPrimitive]
     *
     * @param methodName The name of the method
     * @param returnType The return type of the method
     * @param params The parameters of the method
     */
    abstract fun findVirtualMethod(methodName: String, returnType: Class<*>, vararg params: Class<*>): MethodHandle?
}
