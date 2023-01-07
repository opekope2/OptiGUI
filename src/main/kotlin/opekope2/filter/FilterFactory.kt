package opekope2.filter

import opekope2.optigui.interaction.Interaction
import opekope2.optigui.interaction.registerPreprocessor
import opekope2.optigui.resource.Resource

internal val filterFactories = mutableSetOf<(Resource) -> FilterInfo?>()

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
 * @param factory The filter factory to register
 */
fun registerFilterFactory(factory: (Resource) -> FilterInfo?) {
    filterFactories.add(factory)
}
