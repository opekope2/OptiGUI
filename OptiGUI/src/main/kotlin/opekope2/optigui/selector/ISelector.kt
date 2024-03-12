package opekope2.optigui.selector

import opekope2.optigui.filter.IFilter
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.registry.SelectorRegistry

/**
 * Represents a selector, which creates [filters][IFilter] from their text descriptions, when resources are (re)loaded
 * (when the game starts, the user changes resource packs, or presses F3+T).
 *
 * Each filter accepts an interaction, processes it, and returns a result if a texture should be replaced, and if yes,
 * provides a replacement texture.
 *
 * @see SelectorRegistry
 */
interface ISelector {
    /**
     * Parses the given [selector] and creates a filter from it, if possible.
     *
     * @param selector
     */
    fun createFilter(selector: String): IFilter<Interaction, *>?

    /**
     * Gets the raw selector from the given interaction, which may be parsed by [createFilter] or `null`, if the selector
     * is not available in the given [interaction].
     *
     * @param interaction The interaction to export the selector's string representation from
     */
    fun getRawSelector(interaction: Interaction): String?
}
