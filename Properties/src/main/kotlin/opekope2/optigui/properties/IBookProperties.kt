package opekope2.optigui.properties

/**
 * Properties for books, lecterns, and other book-holding containers.
 *
 * @see net.minecraft.item.WritableBookItem
 * @see net.minecraft.item.WrittenBookItem
 * @see net.minecraft.block.entity.LecternBlockEntity
 * @see opekope2.optigui.properties.impl.BookProperties
 * @see opekope2.optigui.properties.impl.LecternProperties
 */
interface IBookProperties {
    /**
     * Current page of the book.
     */
    val currentPage: Int

    /**
     * Number of pages in the book.
     */
    val pageCount: Int
}
