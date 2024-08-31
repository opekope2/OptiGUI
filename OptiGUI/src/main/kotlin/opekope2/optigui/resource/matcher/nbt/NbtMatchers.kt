package opekope2.optigui.resource.matcher.nbt

import opekope2.optigui.registry.RegistryBase

/**
 * NBT matcher registry.
 */
object NbtMatchers : RegistryBase<String, INbtMatcher>() {
    override fun register(key: String, value: INbtMatcher) {
        if ('$' in key) throw IllegalArgumentException("Key `$key` must not contain dollar sign")
        super.register(key, value)
        super.register("$key$", value)
    }
}
