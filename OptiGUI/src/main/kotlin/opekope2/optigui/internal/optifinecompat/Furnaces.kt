package opekope2.optigui.internal.optifinecompat

import net.minecraft.block.entity.*
import net.minecraft.util.Identifier
import net.minecraft.util.Nameable
import opekope2.filter.*
import opekope2.optifinecompat.properties.FurnaceProperties
import opekope2.optigui.resource.Resource
import opekope2.optigui.service.RegistryLookupService
import opekope2.optigui.service.getService
import opekope2.util.TexturePath
import opekope2.util.splitIgnoreEmpty

private const val CONTAINER = "furnace"
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
    if (resource.properties["container"] != CONTAINER) return null
    val replacement = findReplacementTexture(resource) ?: return null

    val filters = createGeneralFilters(resource)

    val variants = resource.properties["variants"] as? String
    val textures: Set<Identifier> =
        // blast furnace and smoker variants need to be opted in explicitly
        if (variants == null) setOf(TexturePath.FURNACE)
        else {
            val foundVariants = variants.splitIgnoreEmpty(*delimiters).mapNotNull(variantMap::get)

            filters += PreProcessorFilter.nullGuarded(
                { (it.data as? FurnaceProperties)?.variant },
                FilterResult.Mismatch(),
                ContainingFilter(foundVariants)
            )

            foundVariants.mapNotNull(variantToTextureMap::get).toSet()
        }

    filters += PreProcessorFilter(
        { it.texture },
        ContainingFilter(textures)
    )

    return FilterInfo(
        PostProcessorFilter(ConjunctionFilter(filters), replacement),
        textures
    )
}

internal fun processFurnace(furnace: BlockEntity): Any? {
    if (furnace !is AbstractFurnaceBlockEntity) return null
    val lookup = getService<RegistryLookupService>()

    val world = furnace.world ?: return null

    val variant = when (furnace) {
        is FurnaceBlockEntity -> "_furnace"
        is BlastFurnaceBlockEntity -> "_blast_furnace"
        is SmokerBlockEntity -> "_smoker"
        else -> return null
    }

    return FurnaceProperties(
        name = (furnace as? Nameable)?.customName?.string,
        biome = lookup.lookupBiome(world, furnace.pos),
        height = furnace.pos.y,
        variant = variant
    )
}
