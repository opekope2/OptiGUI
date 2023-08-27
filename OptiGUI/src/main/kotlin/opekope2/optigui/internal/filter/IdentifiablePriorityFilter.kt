package opekope2.optigui.internal.filter

import net.minecraft.util.Identifier
import opekope2.lilac.api.resource.IResource
import opekope2.optigui.api.interaction.Interaction
import opekope2.optigui.filter.Filter
import opekope2.optigui.filter.FilterResult
import opekope2.util.dump
import org.slf4j.LoggerFactory

internal class IdentifiablePriorityFilter(
    @Suppress("MemberVisibilityCanBePrivate") val modId: String,
    resource: IResource,
    val filter: Filter<Interaction, Identifier>,
    val replaceableTextures: Set<Identifier>,
    val priority: Int
) : Filter<Interaction, Identifier>(), Iterable<Filter<Interaction, Identifier>> {
    private val resourceId = resource.id
    private val resourcePackName = resource.resourcePack.name

    override fun evaluate(value: Interaction): FilterResult<out Identifier> = try {
        filter.evaluate(value)
    } catch (exception: Exception) {
        logger.warn("Error processing `$filter` by `$modId`: $exception", exception)
        logger.debug(
            "Error processing `$filter` by `$modId`: $exception\n" +
                    "Interaction: $value\n" +
                    filter.dump(), exception
        )
        FilterResult.Skip()
    }

    override fun iterator(): Iterator<Filter<Interaction, Identifier>> = setOf(filter).iterator()

    override fun toString(): String =
        "Identifiable Filter, added by: $modId, resource: $resourceId, resource pack: $resourcePackName"

    private companion object {
        private val logger = LoggerFactory.getLogger("OptiGUI/FilterChain")
    }
}
