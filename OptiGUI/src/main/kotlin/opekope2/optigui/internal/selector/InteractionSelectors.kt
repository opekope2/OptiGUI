package opekope2.optigui.internal.selector

import net.minecraft.util.Identifier
import opekope2.optigui.annotation.Selector
import opekope2.optigui.api.interaction.Interaction
import opekope2.optigui.api.selector.ISelector
import opekope2.optigui.filter.EqualityFilter
import opekope2.optigui.filter.Filter
import opekope2.optigui.filter.PreProcessorFilter


@Selector("interaction.texture")
class InteractionTextureSelector : ISelector {
    override fun createFilter(selector: String): Filter<Interaction, *> =
        PreProcessorFilter(
            { it.texture },
            EqualityFilter(Identifier(selector))
        )
}
