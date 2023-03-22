package opekope2.optigui.internal.optifinecompat

import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import opekope2.filter.*
import opekope2.optifinecompat.properties.OptiFineProperties
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.resource.Resource
import opekope2.optigui.service.RegistryLookupService
import opekope2.optigui.service.getService
import opekope2.util.TexturePath

private const val CONTAINER = "_stonecutter"
private val texture = TexturePath.STONECUTTER

fun createStonecutterFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != CONTAINER) return null
    val replacement = findReplacementTexture(resource) ?: return null

    val filters = createGeneralFilters(resource, CONTAINER, texture)
    val filter = ConjunctionFilter(filters)

    return FilterInfo(
        PostProcessorFilter(
            DisjunctionFilter(
                filter,
                PreProcessorFilter.nullGuarded(
                    ::processStonecutterInteraction,
                    FilterResult.Mismatch(),
                    filter
                )
            ),
            replacement
        ),
        setOf(texture)
    )
}

private typealias StonecutterProperties = OptiFineProperties

private fun processStonecutterInteraction(interaction: Interaction): Interaction? {
    val lookup = getService<RegistryLookupService>()

    val world = interaction.rawInteraction?.world ?: return null
    val pos = BlockPos((interaction.rawInteraction.hitResult as? BlockHitResult)?.blockPos ?: return null)

    return interaction.copy(
        data = StonecutterProperties(
            container = CONTAINER,
            name = null,
            biome = lookup.lookupBiome(world, pos),
            height = pos.y
        )
    )
}
