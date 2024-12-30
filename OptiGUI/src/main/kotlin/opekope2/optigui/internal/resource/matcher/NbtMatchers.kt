package opekope2.optigui.internal.resource.matcher

import opekope2.optigui.resource.matcher.nbt.NbtMatcherRegistry

internal fun register() {
    NbtMatcherRegistry.register(">", NbtComparableFilter.MORE_THAN_DECODER)
    NbtMatcherRegistry.register(">=", NbtComparableFilter.AT_LEAST_DECODER)
    NbtMatcherRegistry.register("=", NbtComparableFilter.EQUAL_TO_DECODER)
    NbtMatcherRegistry.register("=*", NbtComparableFilter.EQUAL_TO_IGNORE_CASE_DECODER)
    NbtMatcherRegistry.register("!=", NbtComparableFilter.NOT_EQUAL_TO_DECODER)
    NbtMatcherRegistry.register("<=", NbtComparableFilter.AT_MOST_DECODER)
    NbtMatcherRegistry.register("<", NbtComparableFilter.LESS_THAN_DECODER)

    NbtMatcherRegistry.register("regex", NbtStringRegexFilter.REGEX_DECODER)
    NbtMatcherRegistry.register("regex*", NbtStringRegexFilter.REGEX_IGNORE_CASE_DECODER)
    NbtMatcherRegistry.register("wildcard", NbtStringRegexFilter.WILDCARD_DECODER)
    NbtMatcherRegistry.register("wildcard*", NbtStringRegexFilter.WILDCARD_IGNORE_CASE_DECODER)

    NbtMatcherRegistry.register("type", NbtTypeFilter.DECODER)

    NbtMatcherRegistry.register("none_of", NONE_OF_DECODER)
    NbtMatcherRegistry.register("any_of", ANY_OF_DECODER)
    NbtMatcherRegistry.register("some_of", SOME_OF_DECODER)
    NbtMatcherRegistry.register("all_of", ALL_OF_DECODER)

    NbtMatcherRegistry.register("keys", NbtKeysFilter.DECODER)

    NbtMatcherRegistry.register("values", NbtValuesFilter.DECODER)

    NbtMatcherRegistry.register("size", NbtCollectionSizeFilter.DECODER)
}
