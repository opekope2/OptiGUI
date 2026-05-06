package opekope2.optigui.filter.transformer

import com.mojang.serialization.Codec
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import opekope2.optigui.filter.NbtTransformerFilter

/**
 * An NBT transformer, which transforms the input NBT compound to its sub-NBT specified dynamically.
 *
 * @see PrefixNbtListIndexTransformer
 * @see SubNbtTransformer
 */
object PrefixSubNbtTransformer : IPrefixNbtTransformer {
    override val type = object : IPrefixNbtTransformer.IType<SubNbtTransformer> {
        override val transformer: PrefixSubNbtTransformer
            get() = PrefixSubNbtTransformer

        override val filterTypeCodec: Codec<NbtTransformerFilter.PrefixType<SubNbtTransformer>> = Codec.STRING.xmap(
            { NbtTransformerFilter.PrefixType("$key$it", SubNbtTransformer(it)) },
            { it.transformer.subNbtKey }
        )
    }

    override fun transform(key: Tag, nbt: Tag, root: Tag) =
        if (key !is StringTag) null
        else getSubNbt(nbt, key.asString)

    /**
     * Gets the sub-NBT specified by [key] from [nbt] if it is a [CompoundTag].
     */
    @JvmStatic
    fun getSubNbt(nbt: Tag, key: String) = if (nbt is CompoundTag) nbt[key] else null
}
