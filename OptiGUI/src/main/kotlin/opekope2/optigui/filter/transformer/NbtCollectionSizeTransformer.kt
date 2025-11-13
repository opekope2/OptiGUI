package opekope2.optigui.filter.transformer

import net.minecraft.nbt.*

/**
 * An NBT transformer, which transforms the input NBT to an [NumericTag] containing its size.
 *
 * @see CompoundTag.size
 * @see CollectionTag.size
 * @see String.length
 */
data object NbtCollectionSizeTransformer : INbtTransformer {
    override fun transform(nbt: Tag, root: Tag) = when (nbt) {
        is CompoundTag -> IntTag.valueOf(nbt.size())
        is CollectionTag<*> -> IntTag.valueOf(nbt.size)
        is StringTag -> IntTag.valueOf(nbt.asString.length)
        else -> null
    }
}
