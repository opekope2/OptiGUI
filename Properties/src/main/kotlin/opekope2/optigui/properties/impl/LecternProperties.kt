package opekope2.optigui.properties.impl

import opekope2.optigui.properties.IBookProperties
import opekope2.optigui.properties.ICommonProperties
import opekope2.optigui.properties.IRedstoneComparatorProperties

/**
 * Implementation of [IBookProperties] for lecterns, plus [ICommonProperties] and [IRedstoneComparatorProperties].
 */
data class LecternProperties(
    val commonProperties: ICommonProperties,
    val redstoneComparatorProperties: IRedstoneComparatorProperties,
    override val currentPage: Int,
    override val pageCount: Int,
) : ICommonProperties by commonProperties, IRedstoneComparatorProperties by redstoneComparatorProperties,
    IBookProperties
