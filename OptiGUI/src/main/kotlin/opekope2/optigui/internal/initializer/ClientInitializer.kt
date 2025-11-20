package opekope2.optigui.internal.initializer

import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer
import opekope2.optigui.filter.*
import opekope2.optigui.filter.comparer.INbtComparer.ComparisonResult.*
import opekope2.optigui.filter.comparer.NbtStringOrNumberComparer
import opekope2.optigui.filter.transformer.*
import opekope2.optigui.interaction.EntityInteraction
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.interaction.nbt_provider.*
import opekope2.optigui.internal.TextureChanger
import opekope2.optigui.internal.config.Config
import opekope2.optigui.internal.config.gui.ButtonEntryGuiProvider
import opekope2.optigui.internal.config.gui.ButtonListEntry
import opekope2.optigui.internal.resource.loader.JsonFilterLoader
import opekope2.optigui.nbt_provider.ILoadTimeNbtProvider
import opekope2.optigui.nbt_provider.NbtFilterNamesNbtProvider

internal object ClientInitializer {
    init {
        TextureChanger.initialize()
        InteractionManager.initialize()

        registerConfig()
        registerInteractionNbtProviders()
        registerPrefixNbtFilters()
        registerNbtFilters()
        registerLoadTimeNbtProviders()
        registerFilterLoaders()
    }

    private fun registerConfig() {
        AutoConfig.getGuiRegistry(Config::class.java)
            .registerTypeProvider(ButtonEntryGuiProvider, ButtonListEntry.IAction::class.java)
        AutoConfig.register(Config::class.java, ::GsonConfigSerializer)
    }

    private fun registerInteractionNbtProviders() {
        IInteractionNbtProvider.register("biome", BiomeNbtProvider(IInteraction::blockPos))
        IInteractionNbtProvider.register("biome_registration", BiomeIdNbtProvider(IInteraction::blockPos))
        IInteractionNbtProvider.register("block_entity", BlockEntityNbtProvider)
        IInteractionNbtProvider.register("block_state", BlockStateNbtProvider)
        IInteractionNbtProvider.register("entity", EntityNbtProvider { (it as? EntityInteraction)?.entity })
        IInteractionNbtProvider.register("hand", HandNbtProvider)
        IInteractionNbtProvider.register("item", ItemNbtProvider)
        IInteractionNbtProvider.register("level", WorldNbtProvider)
        IInteractionNbtProvider.register("player", PlayerNbtProvider)
        IInteractionNbtProvider.register("pos", PositionNbtProvider(IInteraction::blockPos))
        IInteractionNbtProvider.register("structures", StructureBoundingBoxProvider(IInteraction::blockPos))
        IInteractionNbtProvider.register("target", TargetNbtProvider)
        IInteractionNbtProvider.register("time", TimeNbtProvider)
    }

    private fun registerPrefixNbtFilters() {
        INbtFilter.PrefixRegistry.register('@', SubNbtTransformer.Type.Factory)
        INbtFilter.PrefixRegistry.register('#', INbtListFilter.IType.Factory)
    }

    private fun registerNbtFilters() {
        INbtFilter.Registry.register(">", NbtStringOrNumberComparer.CaseSensitive.constantType(MORE))
        INbtFilter.Registry.register(">?", NbtStringOrNumberComparer.CaseSensitive.dynamicType(MORE))
        INbtFilter.Registry.register(">*", NbtStringOrNumberComparer.CaseInsensitive.constantType(MORE))
        INbtFilter.Registry.register(">*?", NbtStringOrNumberComparer.CaseInsensitive.dynamicType(MORE))
        INbtFilter.Registry.register(">=", NbtStringOrNumberComparer.CaseSensitive.constantType(MORE, EQUAL))
        INbtFilter.Registry.register(">=?", NbtStringOrNumberComparer.CaseSensitive.dynamicType(MORE, EQUAL))
        INbtFilter.Registry.register(">=*", NbtStringOrNumberComparer.CaseInsensitive.constantType(MORE, EQUAL))
        INbtFilter.Registry.register(">=*?", NbtStringOrNumberComparer.CaseInsensitive.dynamicType(MORE, EQUAL))
        INbtFilter.Registry.register("=", NbtStringOrNumberComparer.CaseSensitive.constantType(EQUAL))
        INbtFilter.Registry.register("=?", NbtStringOrNumberComparer.CaseSensitive.dynamicType(EQUAL))
        INbtFilter.Registry.register("=*", NbtStringOrNumberComparer.CaseInsensitive.constantType(EQUAL))
        INbtFilter.Registry.register("=*?", NbtStringOrNumberComparer.CaseInsensitive.dynamicType(EQUAL))
        INbtFilter.Registry.register("!=", NbtStringOrNumberComparer.CaseSensitive.constantType(MORE, LESS))
        INbtFilter.Registry.register("!=?", NbtStringOrNumberComparer.CaseSensitive.dynamicType(MORE, LESS))
        INbtFilter.Registry.register("!=*", NbtStringOrNumberComparer.CaseInsensitive.constantType(MORE, LESS))
        INbtFilter.Registry.register("!=*?", NbtStringOrNumberComparer.CaseInsensitive.dynamicType(MORE, LESS))
        INbtFilter.Registry.register("<=", NbtStringOrNumberComparer.CaseSensitive.constantType(EQUAL, LESS))
        INbtFilter.Registry.register("<=?", NbtStringOrNumberComparer.CaseSensitive.dynamicType(EQUAL, LESS))
        INbtFilter.Registry.register("<=*", NbtStringOrNumberComparer.CaseInsensitive.constantType(EQUAL, LESS))
        INbtFilter.Registry.register("<=*?", NbtStringOrNumberComparer.CaseInsensitive.dynamicType(EQUAL, LESS))
        INbtFilter.Registry.register("<", NbtStringOrNumberComparer.CaseSensitive.constantType(LESS))
        INbtFilter.Registry.register("<?", NbtStringOrNumberComparer.CaseSensitive.dynamicType(LESS))
        INbtFilter.Registry.register("<*", NbtStringOrNumberComparer.CaseInsensitive.constantType(LESS))
        INbtFilter.Registry.register("<*?", NbtStringOrNumberComparer.CaseInsensitive.dynamicType(LESS))

        INbtFilter.Registry.register("regex", NbtStringRegexFilter.Type.CASE_SENSITIVE_REGEX)
        INbtFilter.Registry.register("regex*", NbtStringRegexFilter.Type.CASE_INSENSITIVE_REGEX)
        INbtFilter.Registry.register("wildcard", NbtStringRegexFilter.Type.CASE_SENSITIVE_WILDCARD)
        INbtFilter.Registry.register("wildcard*", NbtStringRegexFilter.Type.CASE_INSENSITIVE_WILDCARD)

        INbtFilter.Registry.register("type", NbtTransformerFilter.Type(NbtTypeTransformer))

        INbtFilter.Registry.register("not", NegatedFilter.TYPE)
        INbtFilter.Registry.register("none_of", AggregateFilter.Type.NONE_OF)
        INbtFilter.Registry.register("any_of", AggregateFilter.Type.ANY_OF)
        INbtFilter.Registry.register("some_of", AggregateFilter.Type.SOME_OF)
        INbtFilter.Registry.register("all_of", AggregateFilter.Type.ALL_OF)

        INbtFilter.Registry.register("keys", NbtTransformerFilter.Type(NbtCompoundKeysTransformer))

        INbtFilter.Registry.register("values", NbtTransformerFilter.Type(NbtCompoundValuesTransformer))

        INbtFilter.Registry.register("size", NbtTransformerFilter.Type(NbtCollectionSizeTransformer))

        INbtFilter.Registry.register("if", ConditionalFilter.TYPE)

        INbtFilter.Registry.register("root", NbtTransformerFilter.Type(RootNbtTransformer))
    }

    private fun registerLoadTimeNbtProviders() {
        ILoadTimeNbtProvider.register("filters", NbtFilterNamesNbtProvider)
    }

    private fun registerFilterLoaders() {
        JsonFilterLoader.initialize()
    }
}
