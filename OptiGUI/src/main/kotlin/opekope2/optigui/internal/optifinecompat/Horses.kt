package opekope2.optigui.internal.optifinecompat

import net.minecraft.entity.Entity
import net.minecraft.entity.passive.AbstractHorseEntity
import net.minecraft.entity.passive.LlamaEntity
import net.minecraft.util.Nameable
import opekope2.filter.*
import opekope2.optifinecompat.properties.HorseProperties
import opekope2.optigui.internal.service.EntityVariantLookupService
import opekope2.optigui.resource.Resource
import opekope2.optigui.service.RegistryLookupService
import opekope2.optigui.service.getService
import opekope2.util.TexturePath
import opekope2.util.splitIgnoreEmpty

private const val CONTAINER = "horse"
private val texture = TexturePath.HORSE

fun createHorseFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != CONTAINER) return null
    val replacement = findReplacementTexture(resource) ?: return null

    val filters = createGeneralFilters(resource, CONTAINER, texture)

    filters.addForProperty(resource, "variants", { it.splitIgnoreEmpty(*delimiters) }) { variants ->
        PreProcessorFilter.nullGuarded(
            { (it.data as? HorseProperties)?.variant },
            FilterResult.Mismatch(),
            ContainingFilter(variants)
        )
    }
    filters.addForProperty(resource, "colors", { it.splitIgnoreEmpty(*delimiters) }) { variants ->
        PreProcessorFilter.nullGuarded(
            { it.data as? HorseProperties },
            FilterResult.Mismatch(),
            DisjunctionFilter(
                // Ignore if not llama
                PreProcessorFilter(
                    { it.variant },
                    InequalityFilter("llama")
                ),
                PreProcessorFilter(
                    { it.carpetColor },
                    ContainingFilter(variants)
                )
            )
        )
    }

    return FilterInfo(
        PostProcessorFilter(ConjunctionFilter(filters), replacement),
        setOf(texture)
    )
}

// Referenced from glue for 1.19.3+ camel support
fun processHorse(horse: Entity): Any? {
    if (horse !is AbstractHorseEntity) return null
    val lookup = getService<RegistryLookupService>()
    val variantLookup = getService<EntityVariantLookupService>()

    val world = horse.world ?: return null

    return HorseProperties(
        container = CONTAINER,
        name = (horse as? Nameable)?.customName?.string,
        biome = lookup.lookupBiome(world, horse.blockPos),
        height = horse.blockY,
        variant = variantLookup.getVariant(horse) ?: return null,
        carpetColor = (horse as? LlamaEntity)?.carpetColor?.getName()
    )
}
