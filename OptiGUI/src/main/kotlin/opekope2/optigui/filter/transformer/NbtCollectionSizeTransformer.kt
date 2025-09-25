package opekope2.optigui.filter.transformer

import net.minecraft.nbt.*

/**
 * An NBT transformer, which transforms the input NBT to an [NbtInt] containing its size.
 *
 * @see NbtCompound.getSize
 * @see AbstractNbtList.size
 * @see String.length
 */
data object NbtCollectionSizeTransformer : INbtTransformer {
    override fun transform(nbt: NbtElement): NbtInt? = when (nbt) {
        is NbtCompound -> NbtInt.of(nbt.size)
        is AbstractNbtList<*> -> NbtInt.of(nbt.size)
        is NbtString -> NbtInt.of(nbt.asString().length)
        else -> null
    }
}
