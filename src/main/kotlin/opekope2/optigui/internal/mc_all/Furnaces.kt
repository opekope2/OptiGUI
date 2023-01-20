package opekope2.optigui.internal.mc_all

import net.minecraft.block.entity.*
import net.minecraft.util.Identifier
import net.minecraft.util.Nameable
import opekope2.filter.*
import opekope2.optigui.internal.properties.FurnaceProperties
import opekope2.optigui.provider.IRegistryLookupProvider
import opekope2.optigui.provider.getProvider
import opekope2.optigui.resource.Resource
import opekope2.util.TexturePath
import opekope2.util.resolvePath
import opekope2.util.resolveResource
import opekope2.util.splitIgnoreEmpty
import java.io.File

private const val container = "furnace"
private val variantToTextureMap = mapOf(
    "_furnace" to TexturePath.FURNACE,
    "_blast_furnace" to TexturePath.BLAST_FURNACE,
    "_smoker" to TexturePath.SMOKER
)

// For compatibility, and to filter out invalid
private val variantMap = mapOf(
    "" to "_furnace",
    "_furnace" to "_furnace",
    "_blast" to "_blast_furnace",
    "_blast_furnace" to "_blast_furnace",
    "_smoker" to "_smoker"
)

fun createFurnaceFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != container) return null
    val resFolder = File(resource.id.path).parent.replace('\\', '/')
    val replacement = (resource.properties["texture"] as? String)?.let {
        resource.resourceManager.resolveResource(resolvePath(resFolder, it))
    } ?: return null

    val filters = createGeneralFilters(resource, container)

    val variants = resource.properties["variants"] as? String
    val textures: Set<Identifier> =
        // blast furnace and smoker variants need to be opted in explicitly
        if (variants == null) setOf(TexturePath.FURNACE)
        else {
            val foundVariants = variants.splitIgnoreEmpty(*delimiters).mapNotNull(variantMap::get)
            val variantFilter = ContainingFilter(foundVariants)

            filters += Filter {
                variantFilter.evaluate(
                    (it.data as? FurnaceProperties)?.variant ?: return@Filter FilterResult.Mismatch()
                )
            }

            foundVariants.mapNotNull(variantToTextureMap::get).toSet()
        }

    val textureFilter = ContainingFilter(textures)
    filters += Filter {
        textureFilter.evaluate(it.texture)
    }

    return FilterInfo(
        PostProcessorFilter(ConjunctionFilter(filters), replacement),
        textures
    )
}

internal fun processFurnace(furnace: BlockEntity): Any? {
    if (furnace !is AbstractFurnaceBlockEntity) return null
    val lookup = getProvider<IRegistryLookupProvider>()

    val world = furnace.world ?: return null

    val variant = when (furnace) {
        is FurnaceBlockEntity -> "_furnace"
        is BlastFurnaceBlockEntity -> "_blast_furnace"
        is SmokerBlockEntity -> "_smoker"
        else -> return null
    }

    return FurnaceProperties(
        container = container,
        texture = variantToTextureMap[variant]!!,
        name = (furnace as? Nameable)?.customName?.string,
        biome = lookup.lookupBiome(world, furnace.pos),
        height = furnace.pos.y,
        variant = variant
    )
}
