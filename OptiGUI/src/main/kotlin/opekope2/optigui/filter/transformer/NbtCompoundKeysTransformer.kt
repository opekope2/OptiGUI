package opekope2.optigui.filter.transformer

import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString

/**
 * An NBT transformer, which transforms the input NBT compound to an [NbtList] containing its keys.
 *
 * @see NbtCompound.getKeys
 */
data object NbtCompoundKeysTransformer : INbtTransformer {
    override fun transform(nbt: NbtElement): NbtList? =
        if (nbt !is NbtCompound) null
        else nbt.keys.mapTo(NbtList(), NbtString::of)
}
