package opekope2.optigui.filter

import com.google.common.collect.Iterators
import com.mojang.serialization.Codec
import net.minecraft.nbt.NbtElement
import opekope2.optigui.resource.format.json.JsonFilterResource
import java.util.function.Function

/**
 * An NBT filter, which transforms the input NBT data before passing it to a subfilter.
 */
interface INbtTransformerFilter : INbtFilter, Iterable<INbtFilter> {
    /**
     * The subfilter, which tests the transformed NBT element.
     */
    val subFilter: INbtFilter

    override fun test(nbt: NbtElement): Boolean {
        return subFilter.test(transform(nbt) ?: return false)
    }

    /**
     * Transforms an NBT element.
     *
     * @param nbt The NBT element to transform
     */
    fun transform(nbt: NbtElement): NbtElement?

    override fun iterator(): Iterator<INbtFilter> = Iterators.forArray(subFilter)

    companion object {
        /**
         * Creates a codec for an [INbtTransformerFilter], which has a single subfilter and no other properties.
         *
         * @param T The type of the NBT transformer to create
         * @param constructor The factory method that creates the NBT transformer
         */
        @JvmStatic
        fun <T : INbtTransformerFilter> codec(constructor: Function<INbtFilter, T>): Codec<T> =
            JsonFilterResource.FILTER_CODEC.xmap(constructor, INbtTransformerFilter::subFilter)
    }
}
