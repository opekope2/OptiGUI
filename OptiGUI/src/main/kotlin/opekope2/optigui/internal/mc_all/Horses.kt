package opekope2.optigui.internal.mc_all

import net.minecraft.entity.Entity
import net.minecraft.entity.passive.AbstractHorseEntity
import net.minecraft.util.Nameable
import opekope2.filter.*
import opekope2.optigui.internal.properties.HorseProperties
import opekope2.optigui.internal.service.HorseVariantLookupService
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

    return FilterInfo(
        PostProcessorFilter(ConjunctionFilter(filters), replacement),
        setOf(texture)
    )
}

// Referenced from glue for 1.19.3+ camel support
fun processHorse(horse: Entity): Any? {
    if (horse !is AbstractHorseEntity) return null
    val lookup = getService<RegistryLookupService>()
    val variantLookup = getService<HorseVariantLookupService>()

    val world = horse.world ?: return null

    return HorseProperties(
        container = CONTAINER,
        texture = texture,
        name = (horse as? Nameable)?.customName?.string,
        biome = lookup.lookupBiome(world, horse.blockPos),
        height = horse.blockPos.y,
        variant = variantLookup.getHorseVariant(horse) ?: return null
    )
}
