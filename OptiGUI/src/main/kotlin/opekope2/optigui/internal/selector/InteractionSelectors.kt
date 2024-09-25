package opekope2.optigui.internal.selector

import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import opekope2.optigui.filter.ContainingFilter
import opekope2.optigui.filter.EqualityFilter
import opekope2.optigui.filter.IFilter
import opekope2.optigui.filter.PreProcessorFilter
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.util.joinNotFound
import opekope2.optigui.selector.ISelector

internal class InteractionTextureSelector : ISelector {
    override fun createFilter(selector: String): IFilter<Interaction, *> =
        PreProcessorFilter(
            { it.texture },
            "Get interaction screen texture",
            EqualityFilter(Identifier(selector))
        )

    override fun getRawSelector(interaction: Interaction) = interaction.texture.toString()
}

internal class InteractionHandSelector : AbstractListSelector<Hand>() {
    override fun parseSelector(selector: String) = Hand.values().firstOrNull { it.name.lowercase() == selector }

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid hands: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<Hand>): IFilter<Interaction, *> = PreProcessorFilter(
        { it.data.hand },
        "Get interacting player hand",
        ContainingFilter(parsedSelectors)
    )

    override fun transformInteraction(interaction: Interaction) = interaction.data.hand.name.lowercase()
}
