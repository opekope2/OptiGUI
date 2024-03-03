package opekope2.optigui.internal.selector

import net.minecraft.entity.vehicle.BoatEntity
import opekope2.optigui.filter.ContainingFilter
import opekope2.optigui.filter.IFilter.Result.Companion.mismatch
import opekope2.optigui.filter.PreProcessorFilter
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.util.joinNotFound

internal class ChestBoatVariantSelector : AbstractListSelector<BoatEntity.Type>() {
    override fun parseSelector(selector: String) = BoatEntity.Type.CODEC.byId(selector)

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid chest boat variants: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<BoatEntity.Type>) = PreProcessorFilter.nullGuarded(
        ::transformInteraction,
        "Get chest boat variant",
        mismatch(),
        ContainingFilter(parsedSelectors)
    )

    override fun transformInteraction(interaction: Interaction) =
        (interaction.data?.entityOrRiddenEntity as? BoatEntity)?.variant
}
