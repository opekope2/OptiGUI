package opekope2.optigui.filter

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JavaOps
import net.minecraft.nbt.Tag
import opekope2.optigui.filter.comparer.INbtComparer.ComparisonResult.EQUAL
import opekope2.optigui.filter.comparer.NbtStringOrNumberComparer
import opekope2.optigui.filter.transformer.IPrefixNbtTransformer
import opekope2.optigui.util.NbtFilterEvaluation
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
         * A codec for the types registered in [Registry] or [IPrefixNbtTransformer.Registry].
         */
        @JvmField
        val TYPE_CODEC: Codec<IType<*>> = Codec.STRING.comapFlatMap({
            when {
                it in this -> DataResult.success(getValue(it))
                it.isNotEmpty() && it[0] in IPrefixNbtTransformer.Registry ->
                    IPrefixNbtTransformer.getValue(it[0]).filterTypeCodec.parse(JavaOps.INSTANCE, it.substring(1))

                else -> DataResult.error { "No such filter: $it" }
            }
        }, IType<*>::key)

        /**
         * A codec for [INbtFilter].
         */
        @JvmField
        val CODEC: Codec<INbtFilter> = object : Codec<INbtFilter> {
            private val codec1 by lazy { AggregateFilter.Type.JSON_OBJECT.codec as Codec<INbtFilter> }
            private val codec2 by lazy { AggregateFilter.Type.ANY_OF.codec as Codec<INbtFilter> }
            private val PRIMITIVE_TYPE = NbtStringOrNumberComparer.CASE_SENSITIVE.constantType(EQUAL)
            private val codec3 by lazy { PRIMITIVE_TYPE.codec as Codec<INbtFilter> }

            override fun <T> encode(input: INbtFilter, ops: DynamicOps<T>, prefix: T): DataResult<T> =
                when (input.type) {
                    AggregateFilter.Type.JSON_OBJECT -> codec1.encode(input, ops, prefix)
                    AggregateFilter.Type.ANY_OF -> codec2.encode(input, ops, prefix)
                    PRIMITIVE_TYPE -> codec3.encode(input, ops, prefix)
                    else -> DataResult.error { "Unsupported filter: $input" }
                }

            override fun <T> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<INbtFilter, T>> = when {
                ops.getMap(input).isSuccess -> codec1.decode(ops, input)
                ops.getList(input).isSuccess -> codec2.decode(ops, input)
                ops.getStringValue(input).isSuccess || ops.getNumberValue(input).isSuccess -> codec3.decode(ops, input)
                else -> DataResult.error { "Unsupported filter: $input" }
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
    }
}
