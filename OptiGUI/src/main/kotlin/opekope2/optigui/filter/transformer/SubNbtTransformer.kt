package opekope2.optigui.filter.transformer

import net.minecraft.nbt.Tag

/**
 * An NBT transformer, which transforms the input NBT compound to its sub-NBT specified by [subNbtKey].
 *
 * @param subNbtKey The key of an NBT compound
 * @see NbtListIndexTransformer
 * @see PrefixSubNbtTransformer
 */
data class SubNbtTransformer(val subNbtKey: String) : INbtTransformer {
    override fun transform(nbt: Tag, root: Tag) = PrefixSubNbtTransformer.getSubNbt(nbt, subNbtKey)
}
