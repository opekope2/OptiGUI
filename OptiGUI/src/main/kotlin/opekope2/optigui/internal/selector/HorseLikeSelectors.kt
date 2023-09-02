package opekope2.optigui.internal.selector

import net.minecraft.util.DyeColor
import opekope2.optigui.annotation.Selector
import opekope2.optigui.api.interaction.Interaction
import opekope2.optigui.api.selector.ISelector
import opekope2.optigui.filter.*
import opekope2.util.assertNotEmpty
import opekope2.util.joinNotFound
import opekope2.util.map
import opekope2.optigui.properties.IDonkeyProperties
import opekope2.optigui.properties.ILlamaProperties
import opekope2.util.delimiters
import opekope2.util.splitIgnoreEmpty


@Selector("donkey.has_chest")
class DonkeyChestSelector : ISelector {
    override fun createFilter(selector: String): Filter<Interaction, *> =
        PreProcessorFilter.nullGuarded(
            { (it.data as? IDonkeyProperties)?.hasChest },
            FilterResult.mismatch(),
            EqualityFilter(selector.toBooleanStrict())
        )
}

@Selector("llama.colors")
class LlamaColorSelector : ISelector {
    override fun createFilter(selector: String): Filter<Interaction, *>? =
        selector.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map({ color -> color.takeIf { DyeColor.byName(color, null) != null } }) {
                throw RuntimeException("Invalid colors: ${joinNotFound(it)}")
            }
            ?.assertNotEmpty()
            ?.let { variants ->
                PreProcessorFilter.nullGuarded(
                    { (it.data as? ILlamaProperties)?.carpetColor },
                    FilterResult.mismatch(), // No carpet is mismatch, because at this point, a carpet is required
                    ContainingFilter(variants)
                )
            }
}
