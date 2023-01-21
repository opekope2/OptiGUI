package opekope2.optigui.internal.mc_all

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import opekope2.filter.*
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.properties.OptiFineProperties
import opekope2.optigui.provider.RegistryLookup
import opekope2.optigui.provider.getProvider
import opekope2.optigui.resource.Resource
import opekope2.util.TexturePath

private const val CONTAINER = "inventory"
private val texture = TexturePath.INVENTORY

fun createSurvivalInventoryFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != CONTAINER) return null
    val replacement = findReplacementTexture(resource) ?: return null

    val filters = ConjunctionFilter(createGeneralFilters(resource, CONTAINER, texture))

    return FilterInfo(
        PostProcessorFilter(
            Filter {
                filters.evaluate(processSurvivalInventory(it) ?: return@Filter FilterResult.Mismatch())
            },
            replacement
        ),
        setOf(texture)
    )
}

private typealias SurvivalInventoryProperties = OptiFineProperties

private fun processSurvivalInventory(interaction: Interaction): Interaction? {
    val lookup = getProvider<RegistryLookup>()

    val mc = MinecraftClient.getInstance()
    val world = mc.world ?: return null
    val pos = BlockPos(mc.player?.pos ?: return null)

    return interaction.copy(
        data = SurvivalInventoryProperties(
            container = CONTAINER,
            texture = texture,
            name = null,
            biome = lookup.lookupBiome(world, pos),
            height = pos.y
        )
    )
}
