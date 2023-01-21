package opekope2.optigui.internal.mc_all

import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier
import opekope2.filter.ConjunctionFilter
import opekope2.filter.Filter
import opekope2.filter.FilterInfo
import opekope2.filter.FilterResult.Mismatch
import opekope2.filter.PostProcessorFilter
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.properties.OptiFineProperties
import opekope2.optigui.provider.RegistryLookup
import opekope2.optigui.provider.getProvider
import opekope2.optigui.resource.Resource
import opekope2.util.withResult

private const val CONTAINER = "creative"

fun createCreativeInventoryFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != CONTAINER) return null

    val filters = ConjunctionFilter(createGeneralFilters(resource, CONTAINER))

    val textureMap = mutableMapOf<Identifier, Identifier>()
    for ((key, value) in resource.properties) {
        if ((key as? String)?.startsWith("texture.") == true) {
            val path = key.substring("texture.".length)
            val replacement = findReplacementTexture(resource, value as String) ?: continue

            textureMap[Identifier.tryParse(path) ?: continue] = replacement
            if (!path.endsWith(".png")) { // Workaround
                textureMap[Identifier.tryParse("$path.png") ?: continue] = replacement
            }
        }
    }

    return FilterInfo(
        PostProcessorFilter(
            Filter {
                if (!textureMap.containsKey(it.texture)) return@Filter Mismatch()

                filters.evaluate(processCreativeInventory(it) ?: return@Filter Mismatch())
            }
        ) { input, filterResult ->
            filterResult.withResult(textureMap[input.texture] ?: return@PostProcessorFilter Mismatch())
        },
        textureMap.keys
    )
}

private typealias CreativeInventoryProperties = OptiFineProperties

private fun processCreativeInventory(interaction: Interaction): Interaction? {
    val lookup = getProvider<RegistryLookup>()

    val mc = MinecraftClient.getInstance()
    val world = mc.world ?: return null
    val pos = mc.player?.blockPos ?: return null

    return interaction.copy(
        data = CreativeInventoryProperties(
            container = CONTAINER,
            texture = interaction.texture,
            name = null,
            biome = lookup.lookupBiome(world, pos),
            height = pos.y
        )
    )
}
