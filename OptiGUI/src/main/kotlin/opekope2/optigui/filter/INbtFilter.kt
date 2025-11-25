package opekope2.optigui.filter

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.nbt.Tag
import opekope2.optigui.filter.comparer.INbtComparer.ComparisonResult.EQUAL
import opekope2.optigui.filter.comparer.NbtStringOrNumberComparer
import opekope2.optigui.filter.transformer.IPrefixNbtTransformer
import opekope2.optigui.internal.I18n
import opekope2.optigui.util.NbtFilterEvaluation
import opekope2.optigui.util.dfu.EitherCodec
import opekope2.optigui.util.registry.BiRegistryBase
import org.jetbrains.annotations.ApiStatus

/**
 * Interface for filtering [Tag]s.
 *
 * Any NBT filter, which tests subfilters must implement [Iterable], which returns the subfilters.
 * This is required for proper macro support.
 */
interface INbtFilter {
    /**
     * The type describing this filter.
     */
    val type: IType<*>

    /**
     * Evaluates the filter.
     *
     * @param nbt The current NBT element to test
     * @param root The root NBT element
     */
    fun test(nbt: Tag, root: Tag): Boolean

    /**
     * Collects the sub-filters of this filter and the inputs passed to those.
     * If the input and the root NBT is `null`, the filter should propagate these to any subfilters in order to generate
     * the complete filter tree for debuggability.
     *
     * @param nbt The current NBT element to test or `null`, if no NBT element could be passed to this filter
     * @param root The root NBT element
     */
    fun testSubFilters(nbt: Tag?, root: Tag): List<NbtFilterEvaluation> = listOf()

    /**
     * Returns a string representation of this filter used for debugging purposes.
     * The resulting string should contain [type] and the JSON element this filter was decoded from.
     */
    fun asString() = type.key

    /**
     * A type describing a filter.
     * Implementors should add properties for similar filters parametrized from code in addition to JSON.
     *
     * @param T The class of the filter
     * @see Type
     */
    interface IType<T : INbtFilter> {
        /**
         * Checks if this NBT filter type is registered in [Registry].
         */
        val isRegistered: Boolean
            @ApiStatus.NonExtendable
            get() = containsValue(this)

        /**
         * Gets the key this NBT filter type is registered in [Registry] or throws an exception, if not registered.
         */
        val key: String
            @ApiStatus.NonExtendable
            get() = getKey(this)

        /**
         * The codec used to encode and decode a filter.
         */
        val codec: Codec<T>
    }

    /**
     * Default implementation of [IType].
     *
     * @param name An identifying name for the filter, usually the [Class.getSimpleName]
     * @param codec The codec used to encode and decode a filter
     */
    data class Type<T : INbtFilter>(val name: String, override val codec: Codec<T>) : IType<T> {
        constructor(klass: Class<T>, codec: Codec<T>) : this(klass.simpleName, codec)
    }

    /**
     * NBT filter registry.
     */
    companion object Registry : BiRegistryBase<String, IType<*>>() {
        /**
         * A codec for the registered keys in this registry.
         */
        @JvmField
        val keyCodec: Codec<String> = Codec.STRING.validate {
            if (it in this) DataResult.success(it)
            else DataResult.error(I18n.OPTIGUI_VALIDATION_ERROR_NO_FILTER.supplyTranslation(it))
        }

        /**
         * A codec for the registered NBT filter types in this registry.
         */
        @JvmField
        val typeCodec: Codec<IType<*>> = keyCodec.xmap(::getValue, IType<*>::key)

        /**
         * A codec for the keys present in [Registry] or [IPrefixNbtTransformer.Registry].
         */
        @JvmField
        val KEY_CODEC: Codec<String> = EitherCodec(keyCodec, IPrefixNbtTransformer.keyCodec)

        /**
         * A codec for the types registered in [Registry] or [IPrefixNbtTransformer.Registry].
         */
        @JvmField
        val TYPE_CODEC: Codec<IType<*>> = EitherCodec(typeCodec, IPrefixNbtTransformer.typeCodec)

        /**
         * A codec for [INbtFilter].
         */
        // Lazy-initialized codec to avoid circular reference during class loading
        // INbtFilter::CODEC -> AggregateFilter.Type::codec -> INbtFilter::LIST_CODEC -> INbtFilter::CODEC
        @JvmField
        val CODEC: Codec<INbtFilter> = Codec.lazyInitialized {
            Codec.either(
                EitherCodec(
                    AggregateFilter.Type.JSON_OBJECT.typeValidatedCodec(),
                    AggregateFilter.Type.ANY_OF.typeValidatedCodec()
                ),
                NbtStringOrNumberComparer.CaseSensitive.constantType(EQUAL).typeValidatedCodec()
            ).flatComapMap(Either<*, *>::unwrap) {
                when (it) {
                    is AggregateFilter -> DataResult.success(Either.left(it))
                    is ConstantNbtComparerFilter -> DataResult.success(Either.right(it))
                    else -> DataResult.error(I18n.OPTIGUI_VALIDATION_ERROR_UNSUPPORTED_FILTER.supplyTranslation(it))
                }
            }
        }

        /**
         * A codec for a list of [INbtFilter].
         */
        @JvmField
        val LIST_CODEC: Codec<List<INbtFilter>> = CODEC.listOf()

        override fun validateEntry(key: String, value: IType<*>) {
            super.validateEntry(key, value)
            require(key.isNotEmpty()) { "Key must not be empty" }
            require(key[0] !in IPrefixNbtTransformer.Registry) { "Prefix is already registered: ${key[0]}" }
        }

        private fun <T : INbtFilter> IType<T>.typeValidatedCodec(): Codec<T> = codec.validate {
            if (it.type == this) DataResult.success(it)
            else DataResult.error(I18n.OPTIGUI_VALIDATION_ERROR_WRONG_FILTER_TYPE.supplyTranslation(this, it.type))
        }
    }
}
