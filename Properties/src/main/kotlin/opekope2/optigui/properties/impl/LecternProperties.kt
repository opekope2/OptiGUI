package opekope2.optigui.properties.impl

import opekope2.optigui.properties.IBookProperties
import opekope2.optigui.properties.ICommonProperties
import opekope2.optigui.properties.IRedstoneComparatorProperties
import java.util.function.BiConsumer

/**
 * Implementation of [IBookProperties] for lecterns, plus [ICommonProperties] and [IRedstoneComparatorProperties].
 */
data class LecternProperties(
    private val commonProperties: ICommonProperties,
    private val redstoneComparatorProperties: IRedstoneComparatorProperties,
    override val currentPage: Int,
    override val pageCount: Int,
) : ICommonProperties by commonProperties, IRedstoneComparatorProperties by redstoneComparatorProperties,
    IBookProperties {
    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
        super<ICommonProperties>.writeSelectors(appendSelector)
        super<IRedstoneComparatorProperties>.writeSelectors(appendSelector)
        super<IBookProperties>.writeSelectors(appendSelector)
    }
}
