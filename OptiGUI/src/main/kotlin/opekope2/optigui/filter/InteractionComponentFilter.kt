package opekope2.optigui.filter

import net.minecraft.util.Identifier
import opekope2.optigui.interaction.Interaction
import java.util.*

/**
 * A filter filtering for interaction components.
 *
 * @param componentId The key of an NBT compound to filter for.
 * @param interactionComponentGetters Components of the interaction to filter for
 * @param filter The filter to evaluate on the sub-NBT
 * @see NbtListIndexFilter
 */
class InteractionComponentFilter(
    private val componentId: Identifier,
    private val interactionComponentGetters: EnumSet<InteractionComponentGetter>,
    private val filter: INbtFilter,
) : IInteractionFilter {
    /**
     * Creates a filter filtering for interaction components.
     *
     * @param componentId The key of an NBT compound to filter for.
     * @param interactionComponentGetter Component of the interaction to filter for
     * @param filter The filter to evaluate on the sub-NBT
     */
    constructor(componentId: Identifier, interactionComponentGetter: InteractionComponentGetter, filter: INbtFilter) :
            this(componentId, EnumSet.of(interactionComponentGetter), filter)

    override fun test(interaction: Interaction) =
        interactionComponentGetters.firstNotNullOfOrNull { it.getComponent(interaction, componentId) }
            ?.let(filter::test) ?: false
}
