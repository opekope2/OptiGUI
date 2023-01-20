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
import opekope2.optigui.provider.IRegistryLookupProvider
import opekope2.optigui.provider.getProvider
import opekope2.optigui.resource.Resource
import opekope2.util.resolvePath
import opekope2.util.resolveResource
import opekope2.util.withResult
import java.io.File

private const val container = "creative"

fun createCreativeInventoryFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != container) return null
    val resFolder = File(resource.id.path).parent.replace('\\', '/')

    val filters = ConjunctionFilter(createGeneralFilters(resource, container))

    val textureMap = mutableMapOf<Identifier, Identifier>()
    for ((key, value) in resource.properties) {
        if ((key as? String)?.startsWith("texture.") == true) {
            val path = key.substring("texture.".length)
            val replacement =
                resource.resourceManager.resolveResource(resolvePath(resFolder, value as String)) ?: continue

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
    val lookup = getProvider<IRegistryLookupProvider>()

    val mc = MinecraftClient.getInstance()
    val world = mc.world ?: return null
    val pos = mc.player?.blockPos ?: return null

    return interaction.copy(
        data = CreativeInventoryProperties(
            container = container,
            texture = interaction.texture,
            name = null,
            biome = lookup.lookupBiome(world, pos),
            height = pos.y
        )
    )
}
