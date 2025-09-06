package opekope2.optigui.filter

import com.mojang.serialization.Codec
import net.minecraft.nbt.NbtElement
import opekope2.optigui.registry.RegistryBase
import java.util.function.Predicate

/**
 * Functional interface for filtering [NbtElement]s.
 *
 * Any NBT filter, which tests subfilters must implement [Iterable], which returns the subfilters.
 * This is required for proper macro support.
 */
fun interface INbtFilter : Predicate<NbtElement> {
    override fun and(other: Predicate<in NbtElement>) = INbtFilter { test(it) && other.test(it) }

    override fun negate(): INbtFilter = NegatedFilter(this)

    override fun or(other: Predicate<in NbtElement>) = INbtFilter { test(it) || other.test(it) }

    /**
     * NBT filter registry.
     */
    companion object JsonCodecRegistry : RegistryBase<String, Codec<out INbtFilter>>() {
        /**
         * An instance of [INbtFilter], which matches for every NBT element.
         */
        @JvmField
        val ALWAYS_MATCH = INbtFilter { true }

        /**
         * An instance of [INbtFilter], which matches for no NBT elements.
         */
        @JvmField
        val NEVER_MATCH = INbtFilter { false }

        override fun validateEntry(key: String, value: Codec<out INbtFilter>) {
            super.validateEntry(key, value)
            require(!key.startsWith('@')) { "Key must not start with @: $key" }
            require(!key.startsWith('#') || key == "#none" || key == "#any" || key == "#some" || key == "#all") { "Key must not start with #: $key" }
        }
    }
}
