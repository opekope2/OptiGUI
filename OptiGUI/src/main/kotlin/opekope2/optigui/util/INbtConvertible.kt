package opekope2.optigui.util

import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper

/**
 * Represents an object, which can be written to an [NbtCompound].
 */
interface INbtConvertible {
    /**
     * Writes the object's content to [compound].
     *
     * @param compound The output [NbtCompound] to write contents to
     * @param lookup The registry lookup used to encode NBT
     */
    fun writeNbt(compound: NbtCompound, lookup: RegistryWrapper.WrapperLookup)
}
