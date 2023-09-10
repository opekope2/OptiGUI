package opekope2.optigui.internal.selector

import net.minecraft.entity.vehicle.BoatEntity
import opekope2.optigui.annotation.Selector
import opekope2.optigui.api.interaction.Interaction
import opekope2.optigui.api.selector.ISelector
import opekope2.optigui.filter.ContainingFilter
import opekope2.optigui.filter.IFilter
import opekope2.optigui.filter.PreProcessorFilter
import opekope2.optigui.properties.IChestBoatProperties
import opekope2.util.*


@Selector("chest_boat.variants")
object ChestBoatVariantSelector : ISelector {
    override fun createFilter(selector: String): IFilter<Interaction, *>? =
        selector.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map({ variant -> variant.takeIf { BoatEntity.Type.CODEC.byId(variant) != null } }) {
                throw RuntimeException("Invalid chest boat variants: ${joinNotFound(it)}")
            }
            ?.assertNotEmpty()
            ?.let { variants ->
                PreProcessorFilter.nullGuarded(
                    { (it.data as? IChestBoatProperties)?.variant },
                    IFilter.Result.mismatch(),
                    ContainingFilter(variants)
                )
            }
}
