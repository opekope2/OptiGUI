package opekope2.optigui.internal.filter

import net.minecraft.util.Identifier
import opekope2.filter.Filter
import opekope2.filter.FilterInfo
import opekope2.filter.FilterResult
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.logger
import opekope2.optigui.resource.Resource

internal class IdentifiableFilter(val modId: String, info: FilterInfo, private val resource: Resource) :
    Filter<Interaction, Identifier>, Iterable<Filter<Interaction, Identifier>> {
    val filter: Filter<Interaction, Identifier> by info::filter
    val replaceableTextures: Set<Identifier> by info::replaceableTextures

    override fun evaluate(value: Interaction): FilterResult<out Identifier> = try {
        filter.evaluate(value)
    } catch (exception: Exception) {
        logger.warn("${filter.javaClass.name} threw an exception while evaluating (added by $modId).", exception)
        FilterResult.Skip()
    }

    override fun iterator(): Iterator<Filter<Interaction, Identifier>> = setOf(filter).iterator()

    override fun toString(): String =
        "Identifiable Filter, added by: $modId, resource: ${resource.id}, resource pack: ${resource.resourcePack}"
}
