@file: JvmName("Services")

package opekope2.optigui.service

private val services = mutableMapOf<Class<*>, Any>()

/**
 * Register a service for a class.
 *
 * @param T Java moment. The type of the service interface
 * @param serviceClass Java moment. The class used to query the service. Should be a super interface of the service
 * @param service The implementation of the service interface
 */
fun <T : Any> registerService(serviceClass: Class<T>, service: T): Boolean {
    if (serviceClass in services) return false

    services[serviceClass] = service
    return true
}

/**
 * Register a service for a class.
 *
 * @param T The class used to query the service. Should be a super interface of the service
 * @param service The implementation of the service interface
 */
inline fun <reified T : Any> registerService(service: T) = registerService(T::class.java, service)

/**
 * Returns the service registered in [registerService] or `null`, if no service is registered for the requested type.
 *
 * @param T The type of the service
 * @param serviceClass Java moment. The Java class of the service interface
 */
@Suppress("UNCHECKED_CAST") // Java moment
fun <T> getServiceOrNull(serviceClass: Class<T>): T? = services[serviceClass] as? T

/**
 * Returns the service registered in [registerService] or `null`, if no service is registered for the requested type.
 *
 * @param T The type of the service interface
 */
inline fun <reified T> getServiceOrNull(): T? = getServiceOrNull(T::class.java)

/**
 * Returns the service registered in [registerService] or throws an exception, if no service is registered for the requested type.
 *
 * @param T The type of the service
 * @param serviceClass Java moment. The Java class of the service interface
 */
fun <T> getService(serviceClass: Class<T>): T = getServiceOrNull(serviceClass)
    ?: throw RuntimeException("No service was registered of type '${serviceClass.name}'.")

/**
 * Returns the service registered in [registerService] or throws an exception, if no service is registered for the requested type.
 *
 * @param T The type of the service
 */
inline fun <reified T> getService(): T = getService(T::class.java)
