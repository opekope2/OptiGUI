package opekope2.optigui.filter.transformer

import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import opekope2.optigui.filter.NbtTransformerFilter

/**
 * An NBT transformer, which transforms the input NBT compound to its sub-NBT specified by [subNbtKey].
 *
 * @param subNbtKey The key of an NBT compound
 * @see NbtListIndexTransformer
 */
data class SubNbtTransformer(val subNbtKey: String) : INbtTransformer {
    override fun transform(nbt: NbtElement): NbtElement? =
        if (nbt is NbtCompound) nbt[subNbtKey]
        else null

    /**
     * A type describing an [NbtTransformerFilter] with this transformer.
     *
     * @param subNbtKey The key of an NBT compound
     */
    data class Type(val subNbtKey: String) : NbtTransformerFilter.TypeBase(SubNbtTransformer(subNbtKey))
}
