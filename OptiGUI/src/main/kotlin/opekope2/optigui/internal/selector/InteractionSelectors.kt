package opekope2.optigui.internal.selector

import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import opekope2.optigui.annotation.Selector
import opekope2.optigui.api.interaction.Interaction
import opekope2.optigui.api.selector.ISelector
import opekope2.optigui.filter.ContainingFilter
import opekope2.optigui.filter.EqualityFilter
import opekope2.optigui.filter.IFilter
import opekope2.optigui.filter.PreProcessorFilter
import opekope2.util.*


@Selector("interaction.texture")
class InteractionTextureSelector : ISelector {
    override fun createFilter(selector: String): IFilter<Interaction, *> =
        PreProcessorFilter(
            { it.texture },
            EqualityFilter(Identifier(selector))
        )
}

@Selector("interaction.hand")
object InteractionHandSelector : ISelector {
    private val hands = mapOf(
        "main_hand" to Hand.MAIN_HAND,
        "off_hand" to Hand.OFF_HAND
    )

    override fun createFilter(selector: String): IFilter<Interaction, *>? {
        return selector.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map(hands::get) {
                throw RuntimeException("Invalid hands: ${joinNotFound(it)}")
            }
            ?.let { hands ->
                PreProcessorFilter(
                    { it.rawInteraction?.hand },
                    ContainingFilter(hands)
                )
            }
    }
}
