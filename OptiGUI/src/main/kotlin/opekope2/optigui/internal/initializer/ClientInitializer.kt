package opekope2.optigui.internal.initializer

import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer
import opekope2.optigui.filter.*
import opekope2.optigui.filter.NbtComparableFilter.Result
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.internal.TextureChanger
import opekope2.optigui.internal.config.Config
import opekope2.optigui.internal.config.annotation.RequiresMod
import opekope2.optigui.internal.config.gui.ButtonEntryGuiProvider
import opekope2.optigui.internal.config.gui.ButtonListEntry
import opekope2.optigui.internal.config.gui.ModDependencyGuiTransformer
import opekope2.optigui.resource.format.json.JsonFilterResource
import opekope2.optigui.util.AggregateOperator

internal object ClientInitializer {
    init {
        TextureChanger
        InteractionManager

        registerConfig()
        registerNbtFilters()
    }

    private fun registerConfig() {
        AutoConfig.getGuiRegistry(Config::class.java)
            .registerTypeProvider(ButtonEntryGuiProvider, ButtonListEntry.IAction::class.java)
        AutoConfig.getGuiRegistry(Config::class.java)
            .registerAnnotationTransformer(ModDependencyGuiTransformer, RequiresMod::class.java)
        AutoConfig.register(Config::class.java, ::GsonConfigSerializer)
    }

    private fun registerNbtFilters() {
        INbtFilter.register("#none", NbtListFilter.codec(AggregateOperator.NONE_OF))
        INbtFilter.register("#any", NbtListFilter.codec(AggregateOperator.ANY_OF))
        INbtFilter.register("#some", NbtListFilter.codec(AggregateOperator.SOME_OF))
        INbtFilter.register("#all", NbtListFilter.codec(AggregateOperator.ALL_OF))

        INbtFilter.register(">", NbtComparableFilter.codec(false, Result.MORE))
        INbtFilter.register(">*", NbtComparableFilter.codec(true, Result.MORE))
        INbtFilter.register(">=", NbtComparableFilter.codec(false, Result.MORE, Result.EQUAL))
        INbtFilter.register(">=*", NbtComparableFilter.codec(true, Result.MORE, Result.EQUAL))
        INbtFilter.register("=", NbtComparableFilter.codec(false, Result.EQUAL))
        INbtFilter.register("=*", NbtComparableFilter.codec(true, Result.EQUAL))
        INbtFilter.register("!=", NbtComparableFilter.codec(false, Result.MORE, Result.LESS))
        INbtFilter.register("!=*", NbtComparableFilter.codec(true, Result.MORE, Result.LESS))
        INbtFilter.register("<=", NbtComparableFilter.codec(false, Result.EQUAL, Result.LESS))
        INbtFilter.register("<=*", NbtComparableFilter.codec(true, Result.EQUAL, Result.LESS))
        INbtFilter.register("<", NbtComparableFilter.codec(false, Result.LESS))
        INbtFilter.register("<*", NbtComparableFilter.codec(true, Result.LESS))

        INbtFilter.register("regex", NbtStringRegexFilter.CASE_SENSITIVE_REGEX_CODEC)
        INbtFilter.register("regex*", NbtStringRegexFilter.CASE_INSENSITIVE_REGEX_CODEC)
        INbtFilter.register("wildcard", NbtStringRegexFilter.CASE_SENSITIVE_WILDCARD_CODEC)
        INbtFilter.register("wildcard*", NbtStringRegexFilter.CASE_INSENSITIVE_WILDCARD_CODEC)

        INbtFilter.register("type", INbtTransformerFilter.codec(::NbtTypeFilter))

        INbtFilter.register("not", JsonFilterResource.FILTER_CODEC.xmap(::NegatedFilter, NegatedFilter::subFilter))
        INbtFilter.register("none_of", FilterCollectionFilter.codec(AggregateOperator.NONE_OF))
        INbtFilter.register("any_of", FilterCollectionFilter.codec(AggregateOperator.ANY_OF))
        INbtFilter.register("some_of", FilterCollectionFilter.codec(AggregateOperator.SOME_OF))
        INbtFilter.register("all_of", FilterCollectionFilter.codec(AggregateOperator.ALL_OF))

        INbtFilter.register("keys", INbtTransformerFilter.codec(::NbtCompoundKeysFilter))

        INbtFilter.register("values", INbtTransformerFilter.codec(::NbtCompoundValuesFilter))

        INbtFilter.register("size", INbtTransformerFilter.codec(::NbtCollectionSizeFilter))
    }
}
