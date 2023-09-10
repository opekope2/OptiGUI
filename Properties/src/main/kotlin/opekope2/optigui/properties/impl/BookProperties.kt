package opekope2.optigui.properties.impl

import opekope2.optigui.properties.IBookProperties
import opekope2.optigui.properties.ICommonProperties
import java.util.function.BiConsumer

/**
 * Implementation of [IBookProperties] for book items.
 */
data class BookProperties(
    private val commonProperties: ICommonProperties,
    override val currentPage: Int,
    override val pageCount: Int,
) : ICommonProperties by commonProperties, IBookProperties {
    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
        super<ICommonProperties>.writeSelectors(appendSelector)
        super<IBookProperties>.writeSelectors(appendSelector)
    }
}
