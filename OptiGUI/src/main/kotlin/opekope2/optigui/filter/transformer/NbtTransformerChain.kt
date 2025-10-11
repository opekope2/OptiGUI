package opekope2.optigui.filter.transformer

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.nbt.NbtElement
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.filter.NbtTransformerFilter
import opekope2.optigui.internal.I18n
import java.util.function.Function

/**
 * An NBT transformer, which transforms an NBT element using multiple other NBT transformers after one another.
 */
class NbtTransformerChain(val transformerChain: List<NbtTransformerFilter.TypeBase>) : INbtTransformer {
    override fun transform(nbt: NbtElement): NbtElement? =
        transformerChain.fold(nbt) { nbt, transformer -> transformer.transformer.transform(nbt) ?: return null }

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
    operator fun plus(type: NbtTransformerFilter.TypeBase) = NbtTransformerChain(transformerChain + type)

    companion object {
        /**
         * A codec for [NbtTransformerChain].
         */
        @JvmField
        val CODEC: Codec<NbtTransformerChain> = INbtFilter.typeCodec.comapFlatMap(
            {
                if (it is NbtTransformerFilter.TypeBase) DataResult.success(it)
                else DataResult.error { I18n.OPTIGUI_VALIDATION_ERROR_NOT_AN_NBT_TRANSFORMER.getTranslation(it) }
            },
            Function.identity()
        ).listOf().xmap(::NbtTransformerChain, NbtTransformerChain::transformerChain)
    }
}
