package opekope2.optigui

import net.minecraft.block.entity.BlockEntity
import opekope2.optigui.filter.factory.FilterFactory
import opekope2.optigui.interaction.BlockEntityPreprocessor
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.interaction.FilterFactoryStore
import opekope2.optigui.internal.interaction.IdentifiableBlockEntityPreprocessor
import opekope2.optigui.internal.interaction.PreprocessorStore

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
     * Block entity preprocessors and entity preprocessors supply the [Interaction.data] of the filter the factory creates,
     * which can be registered for every supported block entity and entity using [registerPreprocessor].
     *
     * @param factory The filter factory to register
     */
    fun registerFilterFactory(factory: FilterFactory) = FilterFactoryStore.add(modId, factory)

    /**
     * Registers the preprocessor for a block entity.
     *
     * @param T The block entity type the preprocessor processes
     * @param type Java moment. The block entity type the preprocessor processes
     * @param processor The block entity preprocessor instance
     * @return `true` if registration is successful, `false` if the given block entity already has a preprocessor registered
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : BlockEntity> registerPreprocessor(
        type: Class<out BlockEntity>,
        processor: BlockEntityPreprocessor<T>
    ): Boolean = PreprocessorStore.add(
        type,
        IdentifiableBlockEntityPreprocessor(modId, processor) as IdentifiableBlockEntityPreprocessor<BlockEntity>
    )

    /**
     * Registers the preprocessor for a block entity.
     *
     * @param T The block entity type the preprocessor processes
     * @param processor The block entity preprocessor instance
     * @return `true` if registration is successful, `false` if the given block entity already has a preprocessor registered
     */
    inline fun <reified T : BlockEntity> registerPreprocessor(processor: BlockEntityPreprocessor<T>) =
        registerPreprocessor(T::class.java, processor)
}
