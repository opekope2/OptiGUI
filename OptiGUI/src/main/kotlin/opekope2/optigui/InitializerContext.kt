package opekope2.optigui

import opekope2.optigui.filter.factory.FilterFactory
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.interaction.FilterFactoryStore

/**
 * Initialization context for OptiGUI-compatible mods.
 *
 * Add an [entrypoint](https://fabricmc.net/wiki/documentation:entrypoint) named `optigui` of type [EntryPoint]
 * to obtain an instance of initializer context. You can initialize OptiGUI-related behavior in [EntryPoint.onInitialize].
 */
class InitializerContext internal constructor(private val modId: String) {
    /**
     * Registers a filter factory.
     *
     * Block entity preprocessors and entity preprocessors supply the [Interaction.data] of the filter the factory creates.
     *
     * @param factory The filter factory to register
     */
    fun registerFilterFactory(factory: FilterFactory) = FilterFactoryStore.add(modId, factory)
}
