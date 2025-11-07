package opekope2.optigui.filter

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.nbt.NbtElement
import opekope2.optigui.filter.INbtFilter.PrefixRegistry.containsKey
import opekope2.optigui.filter.comparer.INbtComparer.ComparisonResult.EQUAL
import opekope2.optigui.filter.comparer.NbtStringOrNumberComparer
import opekope2.optigui.internal.I18n
import opekope2.optigui.util.NbtFilterEvaluation
import opekope2.optigui.util.dfu.EitherCodec
import org.jetbrains.annotations.ApiStatus

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
     * If the input and the root NBT is `null`, the filter should propagate these to any subfilters in order to generate
     * the complete filter tree for debuggability.
     *
     * @param nbt The current NBT element to test or `null`, if no NBT element could be passed to this filter
     * @param root The root NBT element
     */
    fun testSubFilters(nbt: NbtElement?, root: NbtElement): List<NbtFilterEvaluation> = listOf()

    /**
     * Returns a string representation of this filter used for debugging purposes.
     * The resulting string should contain [type] and the JSON element this filter was decoded from.
     */
    fun asString() = type.key

    /**
     * An interface describing a filter.
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
            get() = Registry.containsValue(this)

        /**
         * Gets the key this type is registered in [Registry] or throws an exception, if not registered.
         */
        val key: String
            @ApiStatus.NonExtendable
            get() = Registry.getKey(this)

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
     * An interface describing a prefixed filter, that is, which has an additional string input with a prefix character.
     *
     * @param T The class of the filter
     */
    interface IPrefixType<T : INbtFilter> : IType<T> {
        override val isRegistered: Boolean
            @ApiStatus.NonExtendable
            get() = PrefixRegistry.containsValue(factory)

        override val key: String
            @ApiStatus.NonExtendable
            get() = "${factory.prefix}$nonPrefixedKey"

        /**
         * [key] without the prefix character.
         */
        val nonPrefixedKey: String

        /**
         * The factory that created this prefixed NBT filter type.
         */
        val factory: IFactory<out IPrefixType<T>>

        /**
         * A factory that creates [IPrefixType]
         *
         * @param T The type this factory creates
         */
        fun interface IFactory<T : IPrefixType<*>> {
            /**
             * Gets the key this type is registered in [Registry] or throws an exception, if not registered.
             */
            val prefix: Char
                get() = PrefixRegistry.getKey(this)

            /**
             * Checks if a type can be created from the given input string.
             *
             * @param input The input string without prefix to create a type from
             */
            fun canCreateType(input: String) = createType(input) != null

            /**
             * Creates a prefixed NBT filter type from the given string.
             *
             * @param input The input string without prefix to create a type from
             * @return The created type or `null`, if a type couldn't be created
             */
            fun createType(input: String): T?
        }
    }

    /**
     * Base registry for NBT filter registry and prefixed NBT filter registry.
     */
    sealed class RegistryBase<TKey, TValue> : opekope2.optigui.registry.RegistryBase<TKey, TValue>() {
        private val reverseEntries = mutableMapOf<TValue, TKey>()

        /**
         * A codec for the registered keys in this registry.
         */
        val keyCodec: Codec<String> = Codec.STRING.validate {
            if (containsKey(it)) DataResult.success(it)
            else DataResult.error(I18n.OPTIGUI_VALIDATION_ERROR_NO_FILTER.supplyTranslation(it))
        }

        override fun validateEntry(key: TKey, value: TValue) {
            super.validateEntry(key, value)
            require(value !in reverseEntries) { "Type is already registered: $value" }
        }

        override fun register(key: TKey, value: TValue) {
            super.register(key, value)
            reverseEntries[value] = key
        }

        /**
         * Checks if the given key is present in the registry
         *
         * @param key The key to check
         */
        // I cannot name this "contains" because contains is final, and Registry won't be able to implement, which sucks
        protected abstract fun containsKey(key: String): Boolean

        /**
         * Checks if the given value is registered in this registry.
         *
         * @param value The value to check
         */
        fun containsValue(value: TValue) = value in reverseEntries

        @ApiStatus.Internal
        internal fun getKey(value: TValue) = reverseEntries.getValue(value)
    }

    /**
     * NBT filter registry.
     */
    object Registry : RegistryBase<String, IType<*>>() {
        override fun validateEntry(key: String, value: IType<*>) {
            super.validateEntry(key, value)
            require(key.isNotEmpty()) { "Key must not be empty" }
            require(key[0] !in PrefixRegistry) { "Prefix is already registered: ${key[0]}" }
        }

        override fun containsKey(key: String) = key in this
    }

    /**
     * Prefixed NBT filter registry.
     */
    object PrefixRegistry : RegistryBase<Char, IPrefixType.IFactory<*>>() {
        override fun validateEntry(key: Char, value: IPrefixType.IFactory<*>) {
            super.validateEntry(key, value)
            require(Registry.none { it.key.startsWith(key) }) { "A type is already registered with prefix $key" }
        }

        /**
         * Creates a prefixed NBT filter type for the given string, or throws if the prefix is not registered or a type
         * couldn't be created.
         *
         * @param key The prefixed string to create a type from
         */
        fun createType(key: String) =
            requireNotNull(getValue(key[0]).createType(key.substring(1))) { "Failed to create type" }

        /**
         * @see containsKey
         */
        operator fun contains(key: String) = containsKey(key)

        override fun containsKey(key: String) =
            key.isNotEmpty() && key[0] in this && getValue(key[0]).canCreateType(key.substring(1))
    }

    companion object {
        /**
         * A codec for the keys present in this registry.
         */
        @JvmField
        val KEY_CODEC: Codec<String> = EitherCodec(Registry.keyCodec, PrefixRegistry.keyCodec)

        /**
         * A codec for the types registered in this registry.
         */
        @JvmField
        val TYPE_CODEC: Codec<IType<*>> = Codec.STRING.flatXmap({
            when (it) {
                in Registry -> DataResult.success(Registry.getValue(it))
                in PrefixRegistry -> DataResult.success(PrefixRegistry.createType(it))
                else -> DataResult.error(I18n.OPTIGUI_VALIDATION_ERROR_NO_FILTER.supplyTranslation(it))
            }
        }, {
            if (it.isRegistered) DataResult.success(it.key)
            else DataResult.error(I18n.OPTIGUI_VALIDATION_ERROR_NO_FILTER_TYPE.supplyTranslation(it))
        })

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
                    else -> DataResult.error { I18n.OPTIGUI_VALIDATION_ERROR_UNSUPPORTED_FILTER.getTranslation(it) }
                }
            }
        }

        /**
         * A codec for a list of [INbtFilter].
         */
        @JvmField
        val LIST_CODEC: Codec<List<INbtFilter>> = CODEC.listOf()

        private fun <T : INbtFilter> IType<T>.typeValidatedCodec(): Codec<T> = codec.validate {
            if (it.type == this) DataResult.success(it)
            else DataResult.error { I18n.OPTIGUI_VALIDATION_ERROR_WRONG_FILTER_TYPE.getTranslation(this, it.type) }
        }
    }
}
