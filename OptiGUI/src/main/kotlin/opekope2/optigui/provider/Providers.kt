package opekope2.optigui.provider

import opekope2.optigui.exception.ProviderException

private val providers = mutableMapOf<Class<*>, Any>()

/**
 * Register a provider for a class.
 *
 * @param T The type of the provider interface
 * @param providerClass Java moment. The class to query the provider. Should be a base interface
 * @param provider The implementation of the provider interface
 */
fun <T : Any> registerProvider(providerClass: Class<T>, provider: T): Boolean {
    if (providerClass in providers) return false

    providers[providerClass] = provider
    return true
}

/**
 * Register a provider for a class.
 *
 * @param T The type of the provider interface
 * @param provider The implementation of the provider interface
 */
inline fun <reified T : Any> registerProvider(provider: T) = registerProvider(T::class.java, provider)

/**
 * Returns the provider registered in [registerProvider] or `null`, if no provider is registered for the requested type.
 *
 * @param T The type of the provider interface
 * @param providerClass Java moment. The class of the provider interface
 */
@Suppress("UNCHECKED_CAST")
fun <T> getProviderOrNull(providerClass: Class<T>): T? = providers[providerClass] as? T

/**
 * Returns the provider registered in [registerProvider] or `null`, if no provider is registered for the requested type.
 *
 * @param T The type of the provider interface
 */
inline fun <reified T> getProviderOrNull(): T? = getProviderOrNull(T::class.java)

/**
 * Returns the provider registered in [registerProvider] or throws an exception, if no provider is registered for the requested type.
 *
 * @param T The type of the provider interface
 * @param providerClass Java moment. The class of the provider interface
 */
fun <T> getProvider(providerClass: Class<T>): T = getProviderOrNull(providerClass)
    ?: throw ProviderException("No registered providers of type '${providerClass.name}'.")

/**
 * Returns the provider registered in [registerProvider] or throws an exception, if no provider is registered for the requested type.
 *
 * @param T The type of the provider interface
 */
inline fun <reified T> getProvider(): T = getProvider(T::class.java)
