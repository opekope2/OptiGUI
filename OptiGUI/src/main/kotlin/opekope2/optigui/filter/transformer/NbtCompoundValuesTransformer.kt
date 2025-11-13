package opekope2.optigui.filter.transformer

import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList

/**
 * An NBT transformer, which transforms the input NBT compound to an [NbtList] containing its values.
 *
 * @see NbtCompound.get
 */
data object NbtCompoundValuesTransformer : INbtTransformer {
    override fun transform(nbt: NbtElement, root: NbtElement) =
        if (nbt !is NbtCompound) null
        else NbtList().apply {
            // Collection::mapTo uses MutableCollection::add
            // NbtList::add throws, but NbtList::addElement does not
            for (key in nbt.keys) if (!addElement(size, nbt[key])) return null
        }
}
