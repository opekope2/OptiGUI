package opekope2.optigui.internal.optifinecompat

import net.minecraft.entity.Entity
import net.minecraft.entity.vehicle.ChestBoatEntity
import net.minecraft.util.Nameable
import opekope2.filter.*
import opekope2.optifinecompat.properties.ChestBoatProperties
import opekope2.optigui.resource.Resource
import opekope2.optigui.service.RegistryLookupService
import opekope2.optigui.service.getService
import opekope2.util.TexturePath
import opekope2.util.splitIgnoreEmpty

private const val CONTAINER = "_chest_boat"
private val texture = TexturePath.GENERIC_54

fun createChestBoatFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != CONTAINER) return null
    val replacement = findReplacementTexture(resource) ?: return null

    val filters = createGeneralFilters(resource, CONTAINER, texture)

    filters.addForProperty(resource, "variants", { it.splitIgnoreEmpty(*delimiters) }) { variants ->
            PreProcessorFilter.nullGuarded(
            { (it.data as? ChestBoatProperties)?.variant },
            FilterResult.Mismatch(),
            ContainingFilter(variants)
        )
    }

    return FilterInfo(
        PostProcessorFilter(ConjunctionFilter(filters), replacement),
        setOf(texture)
    )
}

internal fun processChestBoat(chestBoat: Entity): Any? {
    if (chestBoat !is ChestBoatEntity) return null
    val lookup = getService<RegistryLookupService>()

    val world = chestBoat.world ?: return null

    return ChestBoatProperties(
        container = CONTAINER,
        name = (chestBoat as? Nameable)?.customName?.string,
        biome = lookup.lookupBiome(world, chestBoat.blockPos),
        height = chestBoat.blockY,
        variant = chestBoat.variant.getName()
    )
}
