package opekope2.optigui.internal.optifinecompat

import net.minecraft.client.MinecraftClient
import opekope2.filter.*
import opekope2.optifinecompat.properties.OptiFineProperties
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.resource.Resource
import opekope2.optigui.service.RegistryLookupService
import opekope2.optigui.service.getService
import opekope2.util.TexturePath

private const val CONTAINER = "inventory"
private val texture = TexturePath.INVENTORY

fun createSurvivalInventoryFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != CONTAINER) return null
    val replacement = findReplacementTexture(resource) ?: return null

    val filters = createGeneralFilters(resource, CONTAINER, texture)
    val filter = ConjunctionFilter(filters)

    return FilterInfo(
        PostProcessorFilter(
            DisjunctionFilter(
                filter,
                PreProcessorFilter.nullGuarded(
                    ::processSurvivalInventory,
                    FilterResult.Mismatch(),
                    filter
                )
            ),
            replacement
        ),
        setOf(texture)
    )
}

private typealias SurvivalInventoryProperties = OptiFineProperties

private fun processSurvivalInventory(interaction: Interaction): Interaction? {
    val lookup = getService<RegistryLookupService>()

    val mc = MinecraftClient.getInstance()
    val world = mc.world ?: return null
    val pos = mc.player?.blockPos ?: return null

    return interaction.copy(
        data = SurvivalInventoryProperties(
            container = CONTAINER,
            texture = texture,
            name = mc.player?.name?.string,
            biome = lookup.lookupBiome(world, pos),
            height = pos.y
        )
    )
}
