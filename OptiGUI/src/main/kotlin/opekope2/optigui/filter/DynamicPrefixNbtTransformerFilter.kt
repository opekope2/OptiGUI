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

/**
 * An NBT filter, which transforms an NBT element, and compares it with another NBT filter.
 *
 * @param subFilter The filter to test the transformed NBT element with
 * @param transformerChain The NBT transformer chain used to extract the key NBT element to be passed to
 *   [Type.transformer]
 * @param type The type describing this filter
 * @see IPrefixNbtTransformer
 */
class DynamicPrefixNbtTransformerFilter(
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

    /**
     * A type describing a [DynamicPrefixNbtTransformerFilter].
     *
     * @param transformer The prefix NBT transformer used to extract the input NBT element to be passed to
     *   [DynamicPrefixNbtTransformerFilter.subFilter] from an NBT element using the given key
     * @param transformerKey The JSON key referring to [DynamicPrefixNbtTransformerFilter.transformerChain]
     */
    data class Type(val transformer: IPrefixNbtTransformer, val transformerKey: String) :
        INbtFilter.IType<DynamicPrefixNbtTransformerFilter> {
        init {
            require(transformer.type.isRegistered) { "Prefix NBT filter is not registered" }
        }

        override val codec: Codec<DynamicPrefixNbtTransformerFilter> = RecordCodecBuilder.create { instance ->
            instance.group(
                NbtTransformerChain.CODEC.field(transformerKey, DynamicPrefixNbtTransformerFilter::transformerChain),
                INbtFilter.CODEC.field(JsonFilterResource.FILTER_KEY, DynamicPrefixNbtTransformerFilter::subFilter),
            ).apply(instance, ::createFilter)
        }

        private fun createFilter(transformerChain: NbtTransformerChain, subFilter: INbtFilter) =
            DynamicPrefixNbtTransformerFilter(subFilter, transformerChain, this)
    }
}
