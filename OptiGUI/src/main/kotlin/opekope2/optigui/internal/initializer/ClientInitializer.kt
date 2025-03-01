package opekope2.optigui.internal.initializer

import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.internal.TextureChanger
import opekope2.optigui.internal.filter.NbtComparableFilter
import opekope2.optigui.internal.operator.*
import opekope2.optigui.operator.INbtOperator

internal object ClientInitializer {
    init {
        TextureChanger
        InteractionManager

        registerNbtOperators()
    }

    private fun registerNbtOperators() {
        INbtOperator.register(">", NbtComparableFilter.MORE_THAN)
        INbtOperator.register(">*", NbtComparableFilter.MORE_THAN_IGNORE_CASE)
        INbtOperator.register(">=", NbtComparableFilter.AT_LEAST)
        INbtOperator.register(">=*", NbtComparableFilter.AT_LEAST_IGNORE_CASE)
        INbtOperator.register("=", NbtComparableFilter.EQUAL_TO)
        INbtOperator.register("=*", NbtComparableFilter.EQUAL_TO_IGNORE_CASE)
        INbtOperator.register("!=", NbtComparableFilter.NOT_EQUAL_TO)
        INbtOperator.register("!=*", NbtComparableFilter.NOT_EQUAL_TO_IGNORE_CASE)
        INbtOperator.register("<=", NbtComparableFilter.AT_MOST)
        INbtOperator.register("<=*", NbtComparableFilter.AT_MOST_IGNORE_CASE)
        INbtOperator.register("<", NbtComparableFilter.LESS_THAN)
        INbtOperator.register("<*", NbtComparableFilter.LESS_THAN_IGNORE_CASE)

        INbtOperator.register("regex", NbtStringRegexOperator.REGEX)
        INbtOperator.register("regex*", NbtStringRegexOperator.REGEX_IGNORE_CASE)
        INbtOperator.register("wildcard", NbtStringRegexOperator.WILDCARD)
        INbtOperator.register("wildcard*", NbtStringRegexOperator.WILDCARD_IGNORE_CASE)

        INbtOperator.register("type", NbtTypeOperator)

        INbtOperator.register("none_of", FilterCollectionOperator.NONE_OF)
        INbtOperator.register("any_of", FilterCollectionOperator.ANY_OF)
        INbtOperator.register("some_of", FilterCollectionOperator.SOME_OF)
        INbtOperator.register("all_of", FilterCollectionOperator.ALL_OF)

        INbtOperator.register("keys", NbtCompoundKeysOperator)

        INbtOperator.register("values", NbtCompoundValuesOperator)

        INbtOperator.register("size", NbtCollectionSizeOperator)
    }
}
