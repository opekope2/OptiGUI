package opekope2.optigui.properties.impl

import opekope2.optigui.properties.ICommonProperties
import opekope2.optigui.properties.IRedstoneComparatorProperties
import java.util.function.BiConsumer

/**
 * Implementation of [IRedstoneComparatorProperties] and [ICommonProperties].
 */
data class CommonRedstoneComparatorProperties(
    val commonProperties: ICommonProperties,
    override val comparatorOutput: Int,
) : ICommonProperties by commonProperties, IRedstoneComparatorProperties {
    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
        super<ICommonProperties>.writeSelectors(appendSelector)
        super<IRedstoneComparatorProperties>.writeSelectors(appendSelector)
    }
}
