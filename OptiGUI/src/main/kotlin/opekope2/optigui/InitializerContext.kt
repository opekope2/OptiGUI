package opekope2.optigui

import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import opekope2.filter.FilterInfo
import opekope2.filter.NullGuardFilter
import opekope2.filter.PreProcessorFilter
import opekope2.optigui.interaction.BlockEntityPreprocessor
import opekope2.optigui.interaction.EntityPreprocessor
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.interaction.IdentifiableBlockEntityPreprocessor
import opekope2.optigui.internal.interaction.IdentifiableEntityPreprocessor
import opekope2.optigui.internal.interaction.PreprocessorStore
import opekope2.optigui.internal.interaction.filterFactories
import opekope2.optigui.resource.Resource

class InitializerContext internal constructor(private val modId: String) {
    /**
     * Registers a filter factory.
     *
     * Filter factories create filters when resources are (re)loaded (when the game starts, or the player presses F3+T)
     *
     * The factory method accepts a resource wrapper, extracts information, and creates a filter (chain)
     * by specifying the processor filter and the textures it can replace.
     * Built-in filters can be found in [opekope2.filter] package.
     * If a filter factory can't process a resource, it returns `null`.
     *
     * Each filter accepts an interaction, processes it, and returns a result whether a texture should be replaced or not,
     * and if yes, provides a replacement texture.
     *
     * Block entity preprocessors and entity preprocessors supply the [Interaction.data] of the filter the factory creates,
     * which can be registered for every supported block entity and entity using [registerPreprocessor].
     *
     * To process anything other than block entities and entities, (for example, creative inventory, anvil screen,
     * most villager job sites), preprocessors are not available. Processing takes place in the filter created by [factory].
     * For example, add an arrow function, a [PreProcessorFilter] and/or [NullGuardFilter] to provide [Interaction.data]
     * using [Interaction.copy], then invoke another filter with the new interaction.
     * You can add a texture filter in front of processing to avoid unnecessary computing.
     *
     * @param factory The filter factory to register
     */
    fun registerFilterFactory(factory: (Resource) -> FilterInfo?) {
        if (modId !in filterFactories) {
            filterFactories[modId] = mutableSetOf()
        }
        filterFactories[modId]!!.add(factory)
    }

    /**
     * Registers the preprocessor for a block entity.
     *
     * @param type Java moment. The block entity type the preprocessor processes
     * @param processor The block entity preprocessor instance
     * @return `true` if registration is successful, `false` if the given block entity already has a preprocessor registered
     */
    fun registerPreprocessor(type: Class<out BlockEntity>, processor: BlockEntityPreprocessor): Boolean =
        PreprocessorStore.add(type, IdentifiableBlockEntityPreprocessor(modId, processor))

    /**
     * Registers the preprocessor for a block entity.
     *
     * @param T The block entity type the preprocessor processes
     * @param processor The block entity preprocessor instance
     * @return `true` if registration is successful, `false` if the given block entity already has a preprocessor registered
     */
    inline fun <reified T : BlockEntity> registerPreprocessor(processor: BlockEntityPreprocessor) =
        registerPreprocessor(T::class.java, processor)

    /**
     * Registers the preprocessor for an entity.
     *
     * @param type Java moment. The entity type the preprocessor processes
     * @param processor The entity preprocessor instance
     * @return `true` if registration is successful, `false` if the given entity already has a preprocessor registered
     */
    fun registerPreprocessor(type: Class<out Entity>, processor: EntityPreprocessor): Boolean =
        PreprocessorStore.add(type, IdentifiableEntityPreprocessor(modId, processor))

    /**
     * Registers the preprocessor for an entity.
     *
     * @param T The entity type the preprocessor processes
     * @param processor The entity preprocessor instance
     * @return `true` if registration is successful, `false` if the given entity already has a preprocessor registered
     */
    inline fun <reified T : Entity> registerPreprocessor(processor: EntityPreprocessor) =
        registerPreprocessor(T::class.java, processor)
}
