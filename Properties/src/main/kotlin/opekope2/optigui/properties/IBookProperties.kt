package opekope2.optigui.properties

import opekope2.optigui.api.interaction.IInteractionData
import java.util.function.BiConsumer

/**
 * Properties for books, lecterns, and other book-holding containers.
 *
 * @see net.minecraft.item.WritableBookItem
 * @see net.minecraft.item.WrittenBookItem
 * @see net.minecraft.block.entity.LecternBlockEntity
 * @see opekope2.optigui.properties.impl.BookProperties
 * @see opekope2.optigui.properties.impl.LecternProperties
 */
interface IBookProperties : IInteractionData {
    /**
     * Current page of the book.
     */
    val currentPage: Int

    /**
     * Number of pages in the book.
     */
    val pageCount: Int

    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
        appendSelector.accept("book.page.current", currentPage.toString())
        appendSelector.accept("book.page.count", pageCount.toString())
    }
}
