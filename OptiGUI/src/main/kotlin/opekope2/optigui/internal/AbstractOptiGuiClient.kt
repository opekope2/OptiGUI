package opekope2.optigui.internal

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
import opekope2.optigui.internal.config.Config
import opekope2.optigui.internal.config.gui.ButtonEntryGuiProvider
import opekope2.optigui.internal.config.gui.ButtonListEntry
import opekope2.optigui.internal.resource.loader.JsonFilterLoader
import opekope2.optigui.nbt_provider.ILoadTimeNbtProvider
import opekope2.optigui.nbt_provider.NbtFilterNamesNbtProvider
import opekope2.optigui.nbt_provider.PrefixNbtFilterNamesNbtProvider
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.MustBeInvokedByOverriders

@ApiStatus.Internal
abstract class AbstractOptiGuiClient {
    abstract val version: String

    abstract fun isModInstalled(modId: String): Boolean

    protected fun initialize() {
        initialize(this)

        TextureChanger.initialize()
        InteractionManager.initialize()

        registerConfig()
        registerInteractionNbtProviders()
        registerPrefixNbtFilters()
        registerNbtFilters()
        registerLoadTimeNbtProviders()
        registerFilterLoaders()
    }

    @MustBeInvokedByOverriders
    protected open fun registerConfig() {
        AutoConfig.getGuiRegistry(Config::class.java)
            .registerTypeProvider(ButtonEntryGuiProvider, ButtonListEntry.IAction::class.java)
        AutoConfig.register(Config::class.java, ::GsonConfigSerializer)
    }

    @MustBeInvokedByOverriders
    protected open fun registerInteractionNbtProviders() {
        IInteractionNbtProvider.register("biome", BiomeNbtProvider(IInteraction::blockPos))
        IInteractionNbtProvider.register("biome_id", BiomeIdNbtProvider(IInteraction::blockPos))
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

    @MustBeInvokedByOverriders
    protected open fun registerPrefixNbtFilters() {
        IPrefixNbtTransformer.register('$', PrefixSubNbtTransformer.type)
        IPrefixNbtTransformer.register('_', PrefixNbtListIndexTransformer.type)
    }

    @MustBeInvokedByOverriders
    protected open fun registerNbtFilters() {
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

        INbtFilter.register("none", NbtListFilter.Type.NONE)
        INbtFilter.register("any", NbtListFilter.Type.ANY)
        INbtFilter.register("some", NbtListFilter.Type.SOME)
        INbtFilter.register("all", NbtListFilter.Type.ALL)

        INbtFilter.register("keys", NbtTransformerFilter.Type(NbtCompoundKeysTransformer))

        INbtFilter.register("values", NbtTransformerFilter.Type(NbtCompoundValuesTransformer))

        INbtFilter.register("size", NbtTransformerFilter.Type(NbtCollectionSizeTransformer))

        INbtFilter.register("if", ConditionalFilter.TYPE)

        INbtFilter.register("root", NbtTransformerFilter.Type(RootNbtTransformer))

        INbtFilter.register("?$", PrefixNbtTransformerFilter.Type(PrefixSubNbtTransformer))
        INbtFilter.register("?_", PrefixNbtTransformerFilter.Type(PrefixNbtListIndexTransformer))
    }

    @MustBeInvokedByOverriders
    protected open fun registerLoadTimeNbtProviders() {
        ILoadTimeNbtProvider.register("filters", NbtFilterNamesNbtProvider)
        ILoadTimeNbtProvider.register("prefix_filters", PrefixNbtFilterNamesNbtProvider)
    }

    @MustBeInvokedByOverriders
    protected open fun registerFilterLoaders() {
        JsonFilterLoader.initialize()
    }

    @ApiStatus.Internal
    companion object {
        @JvmStatic
        lateinit var implementation: AbstractOptiGuiClient
            private set

        @JvmStatic
        private fun initialize(instance: AbstractOptiGuiClient) {
            check(!::implementation.isInitialized) { "Tried to initialize OptiGuiClient twice" }
            implementation = instance
        }

        @Suppress("UnusedReceiverParameter")
        @JvmStatic
        protected fun Any.initialize() {
        }
    }
}
