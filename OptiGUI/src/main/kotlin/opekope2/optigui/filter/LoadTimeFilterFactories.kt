package opekope2.optigui.filter

import opekope2.optigui.registry.RegistryBase

/**
 * Load-time NBT filter registry.
 */
object LoadTimeFilterFactories : RegistryBase<String, ILoadTimeFilterFactory>() {
    override fun validateEntry(key: String, value: ILoadTimeFilterFactory) {
        super.validateEntry(key, value)
        if (key != "if" && !key.startsWith("if.")) {
            throw IllegalArgumentException("Key must start with `if.`")
        }
    }
}
