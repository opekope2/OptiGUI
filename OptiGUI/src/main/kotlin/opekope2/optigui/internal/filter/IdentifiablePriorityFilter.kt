package opekope2.optigui.internal.filter

import net.minecraft.util.Identifier
import opekope2.lilac.api.resource.IResource
import opekope2.optigui.api.interaction.Interaction
import opekope2.optigui.filter.IFilter
import opekope2.util.IIdentifiable
import org.slf4j.LoggerFactory

internal class IdentifiablePriorityFilter(
    override val id: String,
    resource: IResource,
    val filter: IFilter<Interaction, Identifier>,
    val replaceableTextures: Set<Identifier>,
    val priority: Int
) : IFilter<Interaction, Identifier>, IIdentifiable, Iterable<IFilter<Interaction, Identifier>> {
    private val resourceId = resource.id
    private val resourcePackName = resource.resourcePack.name

    override fun evaluate(value: Interaction): IFilter.Result<out Identifier> = try {
        filter.evaluate(value)
    } catch (exception: Exception) {
        logger.warn("Error processing `$filter` by `$id`: $exception", exception)
        logger.debug(
            """
                Error processing `$filter` by `$id`: $exception
                Interaction: $value
                ${filter.createTree()}
            """.trimIndent(),
            exception
        )
        IFilter.Result.skip()
    }

    override fun iterator(): Iterator<IFilter<Interaction, Identifier>> = setOf(filter).iterator()

    override fun toString(): String =
        "Identifiable Filter, added by: $id, resource: $resourceId, resource pack: $resourcePackName"

    private companion object {
        private val logger = LoggerFactory.getLogger("OptiGUI/FilterChain")
    }
}
