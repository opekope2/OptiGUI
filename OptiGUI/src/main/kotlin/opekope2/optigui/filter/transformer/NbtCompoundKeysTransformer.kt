package opekope2.optigui.filter.transformer

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag

/**
 * An NBT transformer, which transforms the input NBT compound to a [ListTag] containing its keys.
 *
 * @see CompoundTag.getAllKeys
 */
data object NbtCompoundKeysTransformer : INbtTransformer {
    override fun transform(nbt: Tag, root: Tag) =
        if (nbt !is CompoundTag) null
        else nbt.allKeys.mapTo(ListTag(), StringTag::valueOf)
}
