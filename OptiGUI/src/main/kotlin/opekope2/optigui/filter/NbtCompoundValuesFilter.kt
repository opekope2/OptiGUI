package opekope2.optigui.filter

import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList

/**
 * An NBT transformer filter, which tests for the input NBT compound's values using the given subfilter.
 *
 * @param subFilter The subfilter to test the compound's values with
 * @see NbtCompound.get
 */
internal class NbtCompoundValuesFilter(override val subFilter: INbtFilter) : INbtTransformerFilter {
    override fun transform(nbt: NbtElement): NbtList? =
        if (nbt !is NbtCompound) null
        else nbt.keys.mapTo(NbtList(), nbt::get)
}
