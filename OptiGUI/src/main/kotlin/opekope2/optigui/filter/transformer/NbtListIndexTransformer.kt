package opekope2.optigui.filter.transformer

import net.minecraft.nbt.Tag

/**
 * An NBT transformer, which transforms the input NBT list to its [index]th element.
 *
 * @param index The index in the NBT list. If it's negative, indexing starts from the back
 * @see SubNbtTransformer
 * @see PrefixNbtListIndexTransformer
 */
data class NbtListIndexTransformer(val index: Int) : INbtTransformer {
    override fun transform(nbt: Tag, root: Tag) = PrefixNbtListIndexTransformer.getElement(nbt, index)
}
