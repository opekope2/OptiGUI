package opekope2.optigui.filter.transformer

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag

/**
 * An NBT transformer, which transforms the input NBT compound to an [ListTag] containing its values.
 *
 * @see CompoundTag.get
 */
data object NbtCompoundValuesTransformer : INbtTransformer {
    override fun transform(nbt: Tag, root: Tag): ListTag? =
        if (nbt !is CompoundTag) null
        else ListTag().apply {
            // Collection::mapTo uses MutableCollection::add
            // NbtList::add throws, but NbtList::addElement does not
            for (key in nbt.allKeys) if (!addTag(size, nbt[key]!!)) return null
        }
}
