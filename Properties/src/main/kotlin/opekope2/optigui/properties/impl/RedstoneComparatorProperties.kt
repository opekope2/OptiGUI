package opekope2.optigui.properties.impl

import opekope2.optigui.properties.IRedstoneComparatorProperties

/**
 * Default implementation of [IRedstoneComparatorProperties].
 */
data class RedstoneComparatorProperties(
    override val comparatorOutput: Int,
) : IRedstoneComparatorProperties
