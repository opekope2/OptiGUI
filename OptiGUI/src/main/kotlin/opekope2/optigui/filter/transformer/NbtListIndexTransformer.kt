package opekope2.optigui.filter.transformer

import net.minecraft.nbt.AbstractNbtList
import net.minecraft.nbt.NbtElement
import opekope2.optigui.filter.NbtTransformerFilter

/**
 * An NBT transformer, which transforms the input NBT list to its [index]th element.
 *
 * @param index The index in the NBT list. If it's negative, indexing starts from the back
 * @see SubNbtTransformer
 */
data class NbtListIndexTransformer(val index: Int) : INbtTransformer {
    override fun transform(nbt: NbtElement, root: NbtElement): NbtElement? = when {
        nbt !is AbstractNbtList<*> -> null
        index in 0 until nbt.size -> nbt[index]
        index in -nbt.size until 0 -> nbt[index + nbt.size]
        else -> null
    }

    /**
     * A type describing an [NbtTransformerFilter] with this transformer.
     *
     * @param index The index in the NBT list. If it's negative, indexing starts from the back
     */
    data class Type(val index: Int) : NbtTransformerFilter.TypeBase(NbtListIndexTransformer(index))
}
