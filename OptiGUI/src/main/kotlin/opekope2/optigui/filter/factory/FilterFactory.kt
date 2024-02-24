package opekope2.optigui.filter.factory

import net.fabricmc.fabric.api.event.player.UseBlockCallback
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.service.InteractionService
import opekope2.optigui.util.identifier

/**
 * A filter factory, which creates filters when resources are (re)loaded (when the game starts, or the player presses F3+T).
 *
 * Each filter factory will be run for each resource when loading resources.
 *
 * Each filter accepts an interaction, processes it, and returns a result whether a texture should be replaced or not,
 * and if yes, provides a replacement texture.
 *
 * To process anything other than block entities and entities, (for example, creative inventory, anvil screen,
 * most villager job sites), these preprocessors are not available. Processing either takes place in
 * [UseBlockCallback.interact], or in the filter created by [createFilter].
 * In the first case, check if the world is client-side, and if the targeted block is added by your mod
 * (check ID with [identifier]), call [InteractionService.interact].
 * In the latter case, you can use [Interaction.rawInteraction]. These details come from [UseBlockCallback.interact],
 * but this runs every game tick, instead of only once, when the interaction happens. You can pass a copy of the
 * interaction with modified details using [Interaction.copy].
 *
 * You can add a filter for [Interaction.texture] as the first filter to avoid unnecessary computing.
 */
fun interface FilterFactory {
    /**
     * Creates a filter descriptor from the resource given with [context].
     *
     * If the resource can't be processed, returns `null`.
     *
     * @param context The context providing the resource and a logging function
     */
    fun createFilter(context: FilterFactoryContext): FilterFactoryResult?
}
