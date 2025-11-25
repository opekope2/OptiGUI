package opekope2.optigui.filter

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import opekope2.optigui.filter.transformer.IPrefixNbtTransformer
import opekope2.optigui.filter.transformer.NbtTransformerChain
import opekope2.optigui.resource.format.json.JsonFilterResource
import opekope2.optigui.util.NbtFilterEvaluation
import opekope2.optigui.util.dfu.field

class PrefixNbtTransformerFilter(
    val subFilter: INbtFilter,
    val transformerChain: NbtTransformerChain,
    override val type: Type
) : INbtFilter {
    override fun test(nbt: Tag, root: Tag): Boolean {
        val key = transformerChain.transform(nbt, root) ?: return false
        val transformed = type.transformer.transform(key, nbt, root) ?: return false
        return subFilter.test(transformed, root)
    }

    override fun testSubFilters(nbt: Tag?, root: Tag): List<NbtFilterEvaluation> {
        val key = nbt?.let { transformerChain.transform(it, root) }
        val transformed = key?.let { type.transformer.transform(it, nbt, root) }
        return subFilter.testSubFilters(transformed, root)
    }

    override fun asString() = buildString {
        append(super.asString())
        transformerChain.transformerChain
            .joinTo(this, prefix = "[", postfix = "]", transform = { StringTag.quoteAndEscape(it.key) })
    }

    data class Type(val transformer: IPrefixNbtTransformer) : INbtFilter.IType<PrefixNbtTransformerFilter> {
        init {
            require(transformer.type.isRegistered) { "Prefix NBT filter is not registered" }
        }

        override val codec: Codec<PrefixNbtTransformerFilter> = RecordCodecBuilder.create { instance ->
            instance.group(
                INbtFilter.CODEC.field(JsonFilterResource.V2.FILTER_KEY, PrefixNbtTransformerFilter::subFilter),
                NbtTransformerChain.CODEC.field("?", PrefixNbtTransformerFilter::transformerChain),
            ).apply(instance, ::createFilter)
        }

        private fun createFilter(subFilter: INbtFilter, transformerChain: NbtTransformerChain) =
            PrefixNbtTransformerFilter(subFilter, transformerChain, this)
    }
}
