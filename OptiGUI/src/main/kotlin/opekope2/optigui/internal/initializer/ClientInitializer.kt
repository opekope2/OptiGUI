package opekope2.optigui.internal.initializer

import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer
import opekope2.optigui.filter.*
import opekope2.optigui.filter.comparer.INbtComparer.ComparisonResult.*
import opekope2.optigui.filter.comparer.NbtStringOrNumberComparer
import opekope2.optigui.filter.transformer.*
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.interaction.nbt_provider.*
import opekope2.optigui.internal.TextureChanger
import opekope2.optigui.internal.config.Config
import opekope2.optigui.internal.config.gui.ButtonEntryGuiProvider
import opekope2.optigui.internal.config.gui.ButtonListEntry
import opekope2.optigui.nbt_provider.ILoadTimeNbtProvider
import opekope2.optigui.nbt_provider.NbtFilterNamesNbtProvider

internal object ClientInitializer {
    init {
        TextureChanger
        InteractionManager

        registerConfig()
        registerInteractionNbtProviders()
        registerPrefixNbtFilters()
        registerNbtFilters()
        registerLoadTimeNbtProviders()
    }

    private fun registerConfig() {
        AutoConfig.getGuiRegistry(Config::class.java)
            .registerTypeProvider(ButtonEntryGuiProvider, ButtonListEntry.IAction::class.java)
        AutoConfig.register(Config::class.java, ::GsonConfigSerializer)
    }

    private fun registerInteractionNbtProviders() {
        IInteractionNbtProvider.register("biome", BiomeNbtProvider)
        IInteractionNbtProvider.register("biome_id", BiomeIdNbtProvider)
        IInteractionNbtProvider.register("block_entity", BlockEntityNbtProvider)
        IInteractionNbtProvider.register("block_state", BlockStateNbtProvider)
        IInteractionNbtProvider.register("entity", EntityNbtProvider)
        IInteractionNbtProvider.register("hand", HandNbtProvider)
        IInteractionNbtProvider.register("item", ItemNbtProvider)
        IInteractionNbtProvider.register("player", PlayerNbtProvider)
        IInteractionNbtProvider.register("player_extra", ExtraPlayerNbtProvider)
        IInteractionNbtProvider.register("pos", PositionNbtProvider)
        IInteractionNbtProvider.register("screen", ScreenNbtProvider)
        IInteractionNbtProvider.register("structures", StructureBoundingBoxProvider)
        IInteractionNbtProvider.register("target", TargetNbtProvider)
        IInteractionNbtProvider.register("time", TimeNbtProvider)
        IInteractionNbtProvider.register("vehicle", VehicleNbtProvider)
        IInteractionNbtProvider.register("world", WorldNbtProvider)
    }

    private fun registerPrefixNbtFilters() {
        INbtFilter.registerPrefix('@', SubNbtTransformer.Type.Factory)
        INbtFilter.registerPrefix('#', INbtListFilter.IType.Factory)
    }

    private fun registerNbtFilters() {
        INbtFilter.register(">", NbtStringOrNumberComparer.CaseSensitive.constantType(MORE))
        INbtFilter.register(">?", NbtStringOrNumberComparer.CaseSensitive.dynamicType(MORE))
        INbtFilter.register(">*", NbtStringOrNumberComparer.CaseInsensitive.constantType(MORE))
        INbtFilter.register(">*?", NbtStringOrNumberComparer.CaseInsensitive.dynamicType(MORE))
        INbtFilter.register(">=", NbtStringOrNumberComparer.CaseSensitive.constantType(MORE, EQUAL))
        INbtFilter.register(">=?", NbtStringOrNumberComparer.CaseSensitive.dynamicType(MORE, EQUAL))
        INbtFilter.register(">=*", NbtStringOrNumberComparer.CaseInsensitive.constantType(MORE, EQUAL))
        INbtFilter.register(">=*?", NbtStringOrNumberComparer.CaseInsensitive.dynamicType(MORE, EQUAL))
        INbtFilter.register("=", NbtStringOrNumberComparer.CaseSensitive.constantType(EQUAL))
        INbtFilter.register("=?", NbtStringOrNumberComparer.CaseSensitive.dynamicType(EQUAL))
        INbtFilter.register("=*", NbtStringOrNumberComparer.CaseInsensitive.constantType(EQUAL))
        INbtFilter.register("=*?", NbtStringOrNumberComparer.CaseInsensitive.dynamicType(EQUAL))
        INbtFilter.register("!=", NbtStringOrNumberComparer.CaseSensitive.constantType(MORE, LESS))
        INbtFilter.register("!=?", NbtStringOrNumberComparer.CaseSensitive.dynamicType(MORE, LESS))
        INbtFilter.register("!=*", NbtStringOrNumberComparer.CaseInsensitive.constantType(MORE, LESS))
        INbtFilter.register("!=*?", NbtStringOrNumberComparer.CaseInsensitive.dynamicType(MORE, LESS))
        INbtFilter.register("<=", NbtStringOrNumberComparer.CaseSensitive.constantType(EQUAL, LESS))
        INbtFilter.register("<=?", NbtStringOrNumberComparer.CaseSensitive.dynamicType(EQUAL, LESS))
        INbtFilter.register("<=*", NbtStringOrNumberComparer.CaseInsensitive.constantType(EQUAL, LESS))
        INbtFilter.register("<=*?", NbtStringOrNumberComparer.CaseInsensitive.dynamicType(EQUAL, LESS))
        INbtFilter.register("<", NbtStringOrNumberComparer.CaseSensitive.constantType(LESS))
        INbtFilter.register("<?", NbtStringOrNumberComparer.CaseSensitive.dynamicType(LESS))
        INbtFilter.register("<*", NbtStringOrNumberComparer.CaseInsensitive.constantType(LESS))
        INbtFilter.register("<*?", NbtStringOrNumberComparer.CaseInsensitive.dynamicType(LESS))

        INbtFilter.register("regex", NbtStringRegexFilter.Type.CASE_SENSITIVE_REGEX)
        INbtFilter.register("regex*", NbtStringRegexFilter.Type.CASE_INSENSITIVE_REGEX)
        INbtFilter.register("wildcard", NbtStringRegexFilter.Type.CASE_SENSITIVE_WILDCARD)
        INbtFilter.register("wildcard*", NbtStringRegexFilter.Type.CASE_INSENSITIVE_WILDCARD)

        INbtFilter.register("type", NbtTransformerFilter.Type(NbtTypeTransformer))

        INbtFilter.register("not", NegatedFilter.TYPE)
        INbtFilter.register("none_of", AggregateFilter.Type.NONE_OF)
        INbtFilter.register("any_of", AggregateFilter.Type.ANY_OF)
        INbtFilter.register("some_of", AggregateFilter.Type.SOME_OF)
        INbtFilter.register("all_of", AggregateFilter.Type.ALL_OF)

        INbtFilter.register("keys", NbtTransformerFilter.Type(NbtCompoundKeysTransformer))

        INbtFilter.register("values", NbtTransformerFilter.Type(NbtCompoundValuesTransformer))

        INbtFilter.register("size", NbtTransformerFilter.Type(NbtCollectionSizeTransformer))

        INbtFilter.register("if", ConditionalFilter.TYPE)

        INbtFilter.register("root", NbtTransformerFilter.Type(RootNbtTransformer))
    }

    private fun registerLoadTimeNbtProviders() {
        ILoadTimeNbtProvider.register("filters", NbtFilterNamesNbtProvider)
    }
}
