package opekope2.optigui.internal.mc_all

import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import opekope2.filter.*
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.properties.OptiFineProperties
import opekope2.optigui.provider.RegistryLookup
import opekope2.optigui.provider.getProvider
import opekope2.optigui.resource.Resource
import opekope2.util.TexturePath

private const val CONTAINER = "crafting"
private val texture = TexturePath.CRAFTING_TABLE

fun createCraftingTableFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != CONTAINER) return null
    val replacement = findReplacementTexture(resource) ?: return null

    val filters = createGeneralFilters(resource, CONTAINER, texture)

    return FilterInfo(
        PostProcessorFilter(
            nullSafePreProcessorFilter(
                ::processCraftingTableInteraction,
                FilterResult.Mismatch(),
                ConjunctionFilter(filters)
            ),
            replacement
        ),
        setOf(texture)
    )
}

private typealias CraftingTableProperties = OptiFineProperties

private fun processCraftingTableInteraction(interaction: Interaction): Interaction? {
    val lookup = getProvider<RegistryLookup>()

    val world = interaction.rawInteraction?.world ?: return null
    val pos = BlockPos((interaction.rawInteraction.hitResult as? BlockHitResult)?.blockPos ?: return null)

    return interaction.copy(
        data = CraftingTableProperties(
            container = CONTAINER,
            texture = texture,
            name = null,
            biome = lookup.lookupBiome(world, pos),
            height = pos.y
        )
    )
}
