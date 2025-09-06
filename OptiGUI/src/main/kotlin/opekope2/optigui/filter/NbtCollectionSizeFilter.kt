package opekope2.optigui.filter

import net.minecraft.nbt.*

/**
 * An NBT transformer filter, which tests for the input NBT's size using the given subfilter.
 *
 * @param subFilter The subfilter to test the collection size with
 * @see NbtCompound.getSize
 * @see AbstractNbtList.size
 * @see String.length
 */
class NbtCollectionSizeFilter(override val subFilter: INbtFilter) : INbtTransformerFilter {
    override fun transform(nbt: NbtElement): NbtInt? = when (nbt) {
        is NbtCompound -> NbtInt.of(nbt.size)
        is AbstractNbtList<*> -> NbtInt.of(nbt.size)
        is NbtString -> NbtInt.of(nbt.asString().length)
        else -> null
    }
}
