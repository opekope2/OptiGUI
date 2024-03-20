package opekope2.optigui.filter

import net.minecraft.text.TextContent
import opekope2.optigui.interaction.Interaction

/**
 * A filter, which returns the first non-null evaluation from [filters].
 * It returns `null`, if none of [filters] return a non-null value, or [filters] is empty.
 *
 * @param TInput The type the filter accepts
 * @param TResult The type the filter returns
 * @param filters The sub-filters to evaluate
 */
open class FirstMatchFilter<TInput, TResult : Any>(private val filters: Collection<IFilter<TInput, out TResult>>) :
    IFilter<TInput, TResult>, Iterable<IFilter<TInput, out TResult>> {
    /**
     * Alternative constructor with variable arguments
     *
     * @param filters The sub-filters to evaluate
     */
    constructor(vararg filters: IFilter<TInput, out TResult>) : this(filters.toList())

    override fun evaluate(input: TInput): TResult? {
        for (filter in filters) {
            val result = filter.evaluate(input)
            if (result != null) {
                if (filter is PostProcessorFilter<*, *, *, *>) {//TODO title
                    val title = filter.getTitle()
                    if (input is Interaction) {
                        if (title.content != TextContent.EMPTY) {
                            input.screen.setTitle(title)
                        }
                        else if (!title.style.isEmpty) {
                            input.screen.setTitle(input.screen.title.copyContentOnly().setStyle(title.style))
                        }
                    }
                }
                return result
            }
        }
        return null
    }

    override fun iterator(): Iterator<IFilter<TInput, out TResult>> = filters.iterator()

    override fun toString(): String = javaClass.name
}
