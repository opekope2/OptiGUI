package opekope2.optigui.filter.transformer

import com.mojang.serialization.Codec
import net.minecraft.nbt.Tag
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.filter.NbtTransformerFilter
import opekope2.optigui.util.registry.BiRegistryBase
import org.jetbrains.annotations.ApiStatus

/**
 * Interface for transforming NBT elements or extracting child NBT elements.
 */
interface IPrefixNbtTransformer {
    val type: IType<*>

    /**
     * Transforms an NBT element.
     *
     * @param key A parameter representing the information to extract
     * @param nbt The NBT element to transform
     * @param root The root NBT element
     * @return The transformed NBT element or `null`, if it can't be transformed
     */
    fun transform(key: Tag, nbt: Tag, root: Tag): Tag?

    /**
     * A type describing a prefix NBT transformer.
     *
     * @param T The class of the NBT transformer this prefix NBT transformer can be converted to by specifying the `key`
     *   argument in [transform] as a constant.
     */
    interface IType<T : INbtTransformer> {
        /**
         * Checks if this prefix NBT transformer type is registered in [Registry].
         */
        val isRegistered: Boolean
            @ApiStatus.NonExtendable
            get() = containsValue(this)

        /**
         * Gets the key this prefix NBT transformer type is registered in [Registry] or throws an exception, if not registered.
         */
        val key: Char
            @ApiStatus.NonExtendable
            get() = getKey(this)

        /**
         * The prefix NBT transformer described by this type.
         */
        val transformer: IPrefixNbtTransformer

        /**
         * The codec used to encode and decode an NBT transformer filter type containing a prefix NBT transformer.
         */
        val filterTypeCodec: Codec<NbtTransformerFilter.PrefixType<T>>
    }

    /**
     * Prefix NBT filter registry.
     */
    companion object Registry : BiRegistryBase<Char, IType<*>>() {
        override fun validateEntry(key: Char, value: IType<*>) {
            super.validateEntry(key, value)
            require(INbtFilter.none { it.key.startsWith(key) }) { "A type is already registered with prefix $key" }
        }
    }
}
