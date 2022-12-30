package opekope2.filter

import net.minecraft.util.Identifier
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.resource.Resource

internal val filterFactories = mutableListOf<(Resource) -> Filter<Interaction, Identifier>?>()

/**
 * Registers a filter factory.
 *
 * Filter factories create filters when resources are (re)loaded (when the game starts or F3+T)
 *
 * The factory method accepts a resource wrapper, extracts information, and creates a filter (chain).
 * Built-in filters can be found in [opekope2.filter] package.
 * If a filter factory can't process a resource, it returns `null`.
 *
 * Each filter accepts an interaction, processes it, and returns a result whether a texture should be replaced or not,
 * and if yes, provides a replacement texture.
 * Please note that all replaceable textures (the textures your filter may replace)
 * should be registered using [opekope2.optigui.interaction.registerReplaceableTexture] for performance reasons.
 *
 * Interaction data providers should be registered for every supported block entity or entity using
 * [opekope2.optigui.interaction.registerInteractionDataFactory].
 */
fun registerFilterFactory(factory: (Resource) -> Filter<Interaction, Identifier>?) {
    if (factory !in filterFactories) filterFactories.add(factory)
}
