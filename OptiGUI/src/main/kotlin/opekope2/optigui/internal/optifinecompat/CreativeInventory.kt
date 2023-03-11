package opekope2.optigui.internal.optifinecompat

import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier
import opekope2.filter.*
import opekope2.filter.FilterResult.Mismatch
import opekope2.optifinecompat.properties.OptiFineProperties
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.resource.Resource
import opekope2.optigui.service.RegistryLookupService
import opekope2.optigui.service.getService
import opekope2.util.withResult

private const val CONTAINER = "creative"

fun createCreativeInventoryFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != CONTAINER) return null

    val filters = createGeneralFilters(resource, CONTAINER)

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

    filters += PreProcessorFilter(
        { it.texture },
        ContainingFilter(textureMap.keys)
    )

    return FilterInfo(
        PostProcessorFilter(
            PreProcessorFilter.nullGuarded(
                ::processCreativeInventory,
                Mismatch(),
                ConjunctionFilter(filters)
            )

        ) { input, filterResult ->
            filterResult.withResult(textureMap[input.texture] ?: return@PostProcessorFilter Mismatch())
        },
        textureMap.keys
    )
}

private typealias CreativeInventoryProperties = OptiFineProperties

private fun processCreativeInventory(interaction: Interaction): Interaction? {
    val lookup = getService<RegistryLookupService>()

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
