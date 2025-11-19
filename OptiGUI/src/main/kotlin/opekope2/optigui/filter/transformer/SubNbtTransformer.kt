package opekope2.optigui.filter.transformer

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.filter.NbtTransformerFilter

/**
 * An NBT transformer, which transforms the input NBT compound to its sub-NBT specified by [subNbtKey].
 *
 * @param subNbtKey The key of an NBT compound
 * @see NbtListIndexTransformer
 */
data class SubNbtTransformer(val subNbtKey: String) : INbtTransformer {
    override fun transform(nbt: Tag, root: Tag) = if (nbt is CompoundTag) nbt[subNbtKey] else null

    /**
     * A type describing an [NbtTransformerFilter] with this transformer.
     *
     * @param nonPrefixedKey The key of an NBT compound
     */
    data class Type(override val nonPrefixedKey: String) : NbtTransformerFilter.IPrefixType {
        override val codec = super.codec

        override val transformer = SubNbtTransformer(nonPrefixedKey)

        override val factory: Factory
            get() = Factory

        /**
         * The factory for [Type].
         */
        companion object Factory : INbtFilter.IPrefixType.IFactory<Type> {
            override fun createType(input: String) = Type(input)
        }
    }
}
