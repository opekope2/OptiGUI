package opekope2.optigui.internal.selector

import net.minecraft.world.InteractionHand
import net.minecraft.resources.ResourceLocation
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
            EqualityFilter(ResourceLocation.parse(selector))
        )

    override fun getRawSelector(interaction: Interaction) = interaction.texture.toString()
}

internal class InteractionHandSelector : AbstractListSelector<InteractionHand>() {
    override fun parseSelector(selector: String) = InteractionHand.entries.firstOrNull { it.name.lowercase() == selector }

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid hands: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<InteractionHand>): IFilter<Interaction, *> = PreProcessorFilter(
        { it.data.hand },
        "Get interacting player hand",
        ContainingFilter(parsedSelectors)
    )

    override fun transformInteraction(interaction: Interaction) = interaction.data.hand.name.lowercase()
}
