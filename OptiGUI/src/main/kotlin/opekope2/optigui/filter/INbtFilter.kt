package opekope2.optigui.filter

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.nbt.NbtElement
import opekope2.optigui.filter.comparer.INbtComparer.ComparisonResult.EQUAL
import opekope2.optigui.filter.comparer.NbtStringOrNumberComparer
import opekope2.optigui.filter.transformer.NbtListIndexTransformer
import opekope2.optigui.filter.transformer.SubNbtTransformer
import opekope2.optigui.internal.I18n
import opekope2.optigui.registry.RegistryBase
import opekope2.optigui.util.NbtFilterEvaluation
import opekope2.optigui.util.dfu.EitherCodec

/**
 * Interface for filtering [NbtElement]s.
 *
 * Any NBT filter, which tests subfilters must implement [Iterable], which returns the subfilters.
 * This is required for proper macro support.
 */
interface INbtFilter {
    /**
     * The type describing this filter.
     */
    val type: IType<out INbtFilter>

    /**
     * Evaluates the filter.
     *
     * @param nbt The current NBT element to test
     * @param root The root NBT element
     */
    fun test(nbt: NbtElement, root: NbtElement): Boolean

    /**
     * Collects the sub-filters of this filter and the inputs passed to those.
     *
     * @param nbt The current NBT element to test
     * @param root The root NBT element
     */
    fun testSubFilters(nbt: NbtElement, root: NbtElement): Collection<NbtFilterEvaluation> = listOf()

    /**
     * An interface describing a filter.
     * Implementors should add properties for similar filters parametrized from code in addition to JSON.
     *
     * @param T The class of the filter
     * @see Type
     */
    interface IType<T : INbtFilter> {
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
     *
     */
    data class Type<T : INbtFilter>(val name: String, override val codec: Codec<T>) : IType<T> {
        constructor(klass: Class<T>, codec: Codec<T>) : this(klass.simpleName, codec)
    }

    /**
     * NBT filter registry.
     */
    companion object Registry : RegistryBase<String, IType<*>>() {
        private val reverseEntries = mutableMapOf<IType<*>, String>()

        /**
         * A codec for the keys present in this registry.
         */
        val keyCodec: Codec<String> = Codec.STRING.validate {
            if (containsKey(it)) DataResult.success(it)
            else DataResult.error(I18n.OPTIGUI_VALIDATION_ERROR_NO_FILTER.supplyTranslation(it))
        }

        /**
         * A codec for the types registered in this registry.
         */
        val typeCodec: Codec<IType<*>> = Codec.STRING.flatXmap(
            {
                if (containsKey(it)) DataResult.success(getType(it))
                else DataResult.error(I18n.OPTIGUI_VALIDATION_ERROR_NO_FILTER.supplyTranslation(it))
            },
            {
                if (containsType(it)) DataResult.success(getKey(it))
                else DataResult.error(I18n.OPTIGUI_VALIDATION_ERROR_NO_FILTER_TYPE.supplyTranslation(it))
            }
        )

        /**
         * A codec for [INbtFilter].
         */
        // Lazy-initialized codec to avoid circular reference during class loading
        val codec: Codec<INbtFilter> = Codec.lazyInitialized {
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
                    else -> DataResult.error { I18n.OPTIGUI_VALIDATION_ERROR_UNSUPPORTED_FILTER.getTranslation(it) }
                }
            }
        }

        /**
         * A codec for a list of [INbtFilter].
         */
        val listCodec: Codec<List<INbtFilter>> = codec.listOf()

        private fun <T : INbtFilter> IType<T>.typeValidatedCodec(): Codec<T> = codec.validate {
            if (it.type == this) DataResult.success(it)
            else DataResult.error { I18n.OPTIGUI_VALIDATION_ERROR_WRONG_FILTER_TYPE.getTranslation(this, it.type) }
        }

        override fun validateEntry(key: String, value: IType<*>) {
            super.validateEntry(key, value)
            require(value !in reverseEntries) { "Type is already registered: $value" }
            require(!key.startsWith('@')) { "Key must not start with @: $key" }
            require(!key.startsWith('#') || key == "#none" || key == "#any" || key == "#some" || key == "#all") { "Key must not start with #: $key" }
            require(value !is SubNbtTransformer.Type) { "Type must not be a sub-NBT transformer" }
            require(value !is NbtListIndexTransformer.Type) { "Type must not be a list index transformer" }
        }

        override fun register(key: String, value: IType<*>) {
            super.register(key, value)
            reverseEntries[value] = key
        }

        /**
         * Checks if the given key is present in this registry, or it represents a [SubNbtTransformer.Type] or
         *   [NbtListIndexTransformer.Type].
         *
         * @param key The key to check
         */
        fun containsKey(key: String) =
            key in super || key.startsWith('@') || key.startsWith('#') && key.substring(1).toIntOrNull() != null

        /**
         * Checks if the given type is registered in this registry, or it is a [SubNbtTransformer.Type] or
         *   [NbtListIndexTransformer.Type].
         *
         * @param type The type to check
         */
        fun containsType(type: IType<*>) =
            type in reverseEntries || type is SubNbtTransformer.Type || type is NbtListIndexTransformer.Type

        /**
         * Gets the key associated with the given type or throws an exception, if the key is not present in this
         *   registry, and it's not a [SubNbtTransformer.Type] or [NbtListIndexTransformer.Type].
         *
         * @param type The type to check
         */
        fun getKey(type: IType<*>) = when (type) {
            is SubNbtTransformer.Type -> "@${type.subNbtKey}"
            is NbtListIndexTransformer.Type -> "#${type.index}"
            else -> reverseEntries.getValue(type)
        }

        /**
         * Gets the type associated with the given key or throws an exception, if the key is not present in this
         *   registry, and it doesn't represent a [SubNbtTransformer.Type] or [NbtListIndexTransformer.Type].
         *
         * @param key The key to check
         */
        fun getType(key: String) = when {
            key.startsWith('@') -> SubNbtTransformer.Type(key.substring(1))
            key.startsWith('#') -> NbtListIndexTransformer.Type(key.substring(1).toInt())
            else -> super.getValue(key)
        }
    }
}
