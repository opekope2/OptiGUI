package opekope2.optigui.properties.impl

import opekope2.optigui.properties.IChestBoatProperties
import opekope2.optigui.properties.ICommonProperties
import java.util.function.BiConsumer

/**
 * Implementation of [IChestBoatProperties] for chest boats.
 */
data class ChestBoatProperties(
    private val commonProperties: ICommonProperties,
    override val variant: String,
) : ICommonProperties by commonProperties, IChestBoatProperties {
    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
        super<ICommonProperties>.writeSelectors(appendSelector)
        super<IChestBoatProperties>.writeSelectors(appendSelector)
    }
}
