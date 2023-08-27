package opekope2.optigui.properties.impl

import opekope2.optigui.properties.ICommonProperties
import opekope2.optigui.properties.IRedstoneComparatorProperties

/**
 * Implementation of [IRedstoneComparatorProperties] and [ICommonProperties].
 */
data class CommonRedstoneComparatorProperties(
    val commonProperties: ICommonProperties,
    override val comparatorOutput: Int,
) : ICommonProperties by commonProperties, IRedstoneComparatorProperties
