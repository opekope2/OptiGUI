package opekope2.optigui.filter

import com.mojang.serialization.Codec

/**
 * Base interface describing prefixed NBT filters that filter NBT lists using a common prefix.
 */
sealed interface INbtListFilter : INbtFilter {
    /**
     * The sub-filter that tests an NBT element.
     */
    val subFilter: INbtFilter

    /**
     * The base type describing an [INbtListFilter].
     */
    sealed interface IType : INbtFilter.IPrefixType<INbtListFilter> {
        override val codec: Codec<INbtListFilter>

        override val factory: Factory
            get() = Factory

        /**
         * The factory for [IType].
         */
        companion object Factory : INbtFilter.IPrefixType.IFactory<IType> {
            override fun createType(input: String) = when (input) {
                "none" -> NbtListFilter.Type.NONE_OF
                "any" -> NbtListFilter.Type.ANY_OF
                "some" -> NbtListFilter.Type.SOME_OF
                "all" -> NbtListFilter.Type.ALL_OF
                else -> when (val number = input.toIntOrNull()) {
                    null -> null
                    else -> NbtListIndexFilter.Type(number)
                }
            }
        }
    }
}
