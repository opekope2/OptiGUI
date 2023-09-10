package opekope2.optigui.properties

import opekope2.optigui.api.interaction.IInteractionData
import java.util.function.BiConsumer

/**
 * Properties for chests.
 *
 * @see net.minecraft.block.entity.ChestBlockEntity
 * @see opekope2.optigui.properties.impl.ChestProperties
 */
interface IChestProperties : IRedstoneComparatorProperties, IInteractionData {
    /**
     * Whether the chest is a large chest.
     */
    val isLarge: Boolean

    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
        super.writeSelectors(appendSelector)
        appendSelector.accept("chest.large", isLarge.toString())
    }
}
