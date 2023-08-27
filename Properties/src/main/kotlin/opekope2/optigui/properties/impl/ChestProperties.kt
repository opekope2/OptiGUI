package opekope2.optigui.properties.impl

import opekope2.optigui.properties.IChestProperties
import opekope2.optigui.properties.ICommonProperties
import opekope2.optigui.properties.IRedstoneComparatorProperties

/**
 * Implementation of [IChestProperties] for chests.
 */
data class ChestProperties(
    val commonProperties: ICommonProperties,
    val redstoneComparatorProperties: IRedstoneComparatorProperties,
    override val isLarge: Boolean,
) : ICommonProperties by commonProperties, IRedstoneComparatorProperties by redstoneComparatorProperties,
    IChestProperties
