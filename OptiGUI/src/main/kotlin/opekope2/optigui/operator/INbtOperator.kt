package opekope2.optigui.operator

import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.registry.RegistryBase

/**
 * An NBT operator, which creates [INbtFilter]s from its serialized representation.
 */
interface INbtOperator {
    fun <T> createFilter(ops: DynamicOps<T>, input: T): DataResult<INbtFilter>

    /**
     * NBT operator registry.
     */
    companion object Registry : RegistryBase<String, INbtOperator>() {
        override fun validateEntry(key: String, value: INbtOperator) {
            super.validateEntry(key, value)
            require(!key.startsWith('@')) { "Key must not start with @" }
            require(!key.startsWith('#')) { "Key must not start with #" }
        }
    }
}
