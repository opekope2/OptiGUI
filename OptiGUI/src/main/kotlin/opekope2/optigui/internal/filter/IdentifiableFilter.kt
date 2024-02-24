package opekope2.optigui.internal.filter

import net.minecraft.util.Identifier
import opekope2.optigui.filter.IFilter
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.logger
import opekope2.optigui.resource.Resource

internal class IdentifiableFilter(
    val modId: String,
    val filter: IFilter<Interaction, Identifier>,
    val replaceableTextures: Set<Identifier>,
    private val resource: Resource
) : IFilter<Interaction, Identifier>, Iterable<IFilter<Interaction, Identifier>> {
    override fun evaluate(value: Interaction): IFilter.Result<out Identifier> = try {
        filter.evaluate(value)
    } catch (exception: Exception) {
        logger.warn("${filter.javaClass.name} threw an exception while evaluating (added by $modId).", exception)
        IFilter.Result.Skip
    }

    override fun iterator(): Iterator<IFilter<Interaction, Identifier>> = setOf(filter).iterator()

    override fun toString(): String =
        "Identifiable Filter, added by: $modId, resource: ${resource.id}, resource pack: ${resource.resourcePack}"
}
