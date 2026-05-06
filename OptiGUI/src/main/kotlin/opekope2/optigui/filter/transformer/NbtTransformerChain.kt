package opekope2.optigui.filter.transformer

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.nbt.Tag
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.filter.NbtTransformerFilter
import java.util.function.Function

/**
 * An NBT transformer, which transforms an NBT element using multiple other NBT transformers after one another.
 */
class NbtTransformerChain(val transformerChain: List<NbtTransformerFilter.IType>) : INbtTransformer {
    override fun transform(nbt: Tag, root: Tag): Tag? =
        transformerChain.fold(nbt) { nbt, transformer -> transformer.transformer.transform(nbt, root) ?: return null }

    /**
     * Merges two NBT transformer chains.
     *
     * @param other The transformer chain to append to the end of the current transformer chain
     * @return A new [NbtTransformerChain], which consists of [transformerChain] of `this` followed by
     *   [transformerChain] of [other]
     */
    operator fun plus(other: NbtTransformerChain) = NbtTransformerChain(transformerChain + other.transformerChain)

    /**
     * Adds an NBT transformer to the transformer chain.
     *
     * @param type representing the NBT transformer to append to the end of the current transformer chain
     * @return A new [NbtTransformerChain] with [type] appended to the end of [transformerChain]
     */
    operator fun plus(type: NbtTransformerFilter.IType) = NbtTransformerChain(transformerChain + type)

    companion object {
        /**
         * A codec for [NbtTransformerChain].
         */
        @JvmField
        val CODEC: Codec<NbtTransformerChain> = INbtFilter.TYPE_CODEC.comapFlatMap(
            {
                if (it is NbtTransformerFilter.IType) DataResult.success(it)
                else DataResult.error { "Not an NBT transformer type: $it" }
            },
            Function.identity()
        ).listOf().xmap(::NbtTransformerChain, NbtTransformerChain::transformerChain)
    }
}
