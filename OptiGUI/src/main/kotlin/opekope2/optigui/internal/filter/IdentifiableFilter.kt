package opekope2.optigui.internal.filter

import net.minecraft.util.Identifier
import opekope2.filter.Filter
import opekope2.filter.FilterInfo
import opekope2.filter.FilterResult
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.logger

internal class IdentifiableFilter(val modId: String, info: FilterInfo) : Filter<Interaction, Identifier>,
    Iterable<Filter<Interaction, Identifier>> {
    val filter: Filter<Interaction, Identifier> = info.filter
    val replaceableTextures: Set<Identifier> = info.replaceableTextures

    override fun evaluate(value: Interaction): FilterResult<out Identifier> {
        return try {
            filter.evaluate(value)
        } catch (exception: Exception) {
            logger.warn("${filter.javaClass.name} threw an exception while evaluating (added by $modId).", exception)
            FilterResult.Skip()
        }
    }

    override fun iterator(): Iterator<Filter<Interaction, Identifier>> = setOf(filter).iterator()

    override fun toString(): String = "Identifiable Filter, added by: $modId"
}
