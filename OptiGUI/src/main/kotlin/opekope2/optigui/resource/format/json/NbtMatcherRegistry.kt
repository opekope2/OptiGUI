package opekope2.optigui.resource.format.json

import com.mojang.serialization.Decoder
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.registry.RegistryBase

/**
 * NBT matcher registry storing decoders, which creates NBT filters from its serialized representation.
 */
object NbtMatcherRegistry : RegistryBase<String, Decoder<INbtFilter>>() {
    override fun validateEntry(key: String, value: Decoder<INbtFilter>) {
        super.validateEntry(key, value)
        require(!key.startsWith('@')) { "Key must not start with @" }
        require(!key.startsWith('#')) { "Key must not start with #" }
    }
}
