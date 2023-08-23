package opekope2.optigui.properties.impl

import opekope2.optigui.properties.IBookProperties
import opekope2.optigui.properties.ICommonProperties

/**
 * Implementation of [IBookProperties] for book items.
 */
data class BookProperties(
    val commonProperties: ICommonProperties,
    override val currentPage: Int,
    override val pageCount: Int,
) : ICommonProperties by commonProperties, IBookProperties
