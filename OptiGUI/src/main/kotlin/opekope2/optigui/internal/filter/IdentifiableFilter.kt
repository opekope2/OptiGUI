package opekope2.optigui.internal.filter

import net.minecraft.util.Identifier
import opekope2.filter.Filter
import opekope2.filter.FilterResult
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.logger
import opekope2.optigui.resource.Resource
import opekope2.util.dump

internal class IdentifiableFilter(
    val modId: String,
    val filter: Filter<Interaction, Identifier>,
    val replaceableTextures: Set<Identifier>,
    private val resource: Resource
) : Filter<Interaction, Identifier>, Iterable<Filter<Interaction, Identifier>> {
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
