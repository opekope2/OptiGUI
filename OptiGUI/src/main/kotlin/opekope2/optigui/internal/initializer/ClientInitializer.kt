package opekope2.optigui.internal.initializer

import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer
import opekope2.optigui.filter.FilterCollectionFilter
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.internal.TextureChanger
import opekope2.optigui.internal.config.Config
import opekope2.optigui.internal.config.annotation.RequiresMod
import opekope2.optigui.internal.config.gui.ButtonEntryGuiProvider
import opekope2.optigui.internal.config.gui.ButtonListEntry
import opekope2.optigui.internal.config.gui.ModDependencyGuiTransformer
import opekope2.optigui.internal.filter.*
import opekope2.optigui.internal.filter.NbtComparableFilter.Result
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
        INbtFilter.register("#none", NbtListFilter.decoder(AggregateOperator.NONE_OF))
        INbtFilter.register("#any", NbtListFilter.decoder(AggregateOperator.ANY_OF))
        INbtFilter.register("#some", NbtListFilter.decoder(AggregateOperator.SOME_OF))
        INbtFilter.register("#all", NbtListFilter.decoder(AggregateOperator.ALL_OF))

        INbtFilter.register(">", NbtComparableFilter.Decoder(Result.MORE.mask, false))
        INbtFilter.register(">*", NbtComparableFilter.Decoder(Result.MORE.mask, true))
        INbtFilter.register(">=", NbtComparableFilter.Decoder(Result.MORE.mask or Result.EQUAL.mask, false))
        INbtFilter.register(">=*", NbtComparableFilter.Decoder(Result.MORE.mask or Result.EQUAL.mask, true))
        INbtFilter.register("=", NbtComparableFilter.EQUAL_DECODER)
        INbtFilter.register("=*", NbtComparableFilter.Decoder(Result.EQUAL.mask, true))
        INbtFilter.register("!=", NbtComparableFilter.Decoder(Result.MORE.mask or Result.LESS.mask, false))
        INbtFilter.register("!=*", NbtComparableFilter.Decoder(Result.MORE.mask or Result.LESS.mask, true))
        INbtFilter.register("<=", NbtComparableFilter.Decoder(Result.EQUAL.mask or Result.LESS.mask, false))
        INbtFilter.register("<=*", NbtComparableFilter.Decoder(Result.EQUAL.mask or Result.LESS.mask, true))
        INbtFilter.register("<", NbtComparableFilter.Decoder(Result.LESS.mask, false))
        INbtFilter.register("<*", NbtComparableFilter.Decoder(Result.LESS.mask, true))

        INbtFilter.register("regex", NbtStringRegexFilter.Decoder.Regex(false))
        INbtFilter.register("regex*", NbtStringRegexFilter.Decoder.Regex(true))
        INbtFilter.register("wildcard", NbtStringRegexFilter.Decoder.Wildcard(false))
        INbtFilter.register("wildcard*", NbtStringRegexFilter.Decoder.Wildcard(true))

        INbtFilter.register("type", NbtTypeFilter.DECODER)

        INbtFilter.register("not", INbtFilter.DECODER.map(::NegatedFilter))
        INbtFilter.register("none_of", FilterCollectionFilter.Decoder(AggregateOperator.NONE_OF))
        INbtFilter.register("any_of", FilterCollectionFilter.Decoder(AggregateOperator.ANY_OF))
        INbtFilter.register("some_of", FilterCollectionFilter.Decoder(AggregateOperator.SOME_OF))
        INbtFilter.register("all_of", FilterCollectionFilter.Decoder(AggregateOperator.ALL_OF))

        INbtFilter.register("keys", INbtFilter.DECODER.map(::NbtCompoundKeysFilter))

        INbtFilter.register("values", INbtFilter.DECODER.map(::NbtCompoundValuesFilter))

        INbtFilter.register("size", INbtFilter.DECODER.map(::NbtCollectionSizeFilter))
    }
}
