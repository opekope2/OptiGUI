package opekope2.optigui.internal.selector

import net.minecraft.entity.vehicle.BoatEntity
import opekope2.optigui.annotation.Selector
import opekope2.optigui.api.interaction.Interaction
import opekope2.optigui.api.selector.ISelector
import opekope2.optigui.filter.ContainingFilter
import opekope2.optigui.filter.Filter
import opekope2.optigui.filter.FilterResult
import opekope2.optigui.filter.PreProcessorFilter
import opekope2.util.assertNotEmpty
import opekope2.util.joinNotFound
import opekope2.util.map
import opekope2.optigui.properties.IChestBoatProperties
import opekope2.util.delimiters
import opekope2.util.splitIgnoreEmpty


@Selector("chest_boat.variants")
class ChestBoatVariantSelector : ISelector {
    override fun createFilter(selector: String): Filter<Interaction, *>? =
        selector.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map({ variant -> variant.takeIf { BoatEntity.Type.CODEC.byId(variant) != null } }) {
                throw RuntimeException("Invalid chest boat variants: ${joinNotFound(it)}")
            }
            ?.assertNotEmpty()
            ?.let { variants ->
                PreProcessorFilter.nullGuarded(
                    { (it.data as? IChestBoatProperties)?.variant },
                    FilterResult.mismatch(),
                    ContainingFilter(variants)
                )
            }
}
