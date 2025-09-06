package opekope2.optigui.filter

import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString

/**
 * An NBT transformer filter, which tests for the input NBT compound's keys using the given subfilter.
 *
 * @param subFilter The subfilter to test the compound's keys with
 * @see NbtCompound.getKeys
 */
class NbtCompoundKeysFilter(override val subFilter: INbtFilter) : INbtTransformerFilter {
    override fun transform(nbt: NbtElement): NbtList? =
        if (nbt !is NbtCompound) null
        else nbt.keys.mapTo(NbtList(), NbtString::of)
}
