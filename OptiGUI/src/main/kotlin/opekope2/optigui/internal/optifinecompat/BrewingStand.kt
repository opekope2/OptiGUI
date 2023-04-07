package opekope2.optigui.internal.optifinecompat

import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BrewingStandBlockEntity
import net.minecraft.util.Nameable
import opekope2.filter.FilterInfo
import opekope2.filter.PostProcessorFilter
import opekope2.optifinecompat.FilterBuilder
import opekope2.optifinecompat.properties.OptiFineProperties
import opekope2.optigui.resource.Resource
import opekope2.optigui.service.RegistryLookupService
import opekope2.optigui.service.getService
import opekope2.util.TexturePath

private const val CONTAINER = "brewing_stand"
private val texture = TexturePath.BREWING_STAND

fun createBrewingStandFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != CONTAINER) return null
    val replacement = findReplacementTexture(resource) ?: return null

    val filter = FilterBuilder.build(resource) {
        setReplaceableTextures(texture)
        addGeneralFilters<BrewingStandProperties>()
    }

    return FilterInfo(
        PostProcessorFilter(filter, replacement),
        setOf(texture)
    )
}

private typealias BrewingStandProperties = OptiFineProperties

internal fun processBrewingStand(brewingStand: BlockEntity): Any? {
    if (brewingStand !is BrewingStandBlockEntity) return null
    val lookup = getService<RegistryLookupService>()

    val world = brewingStand.world ?: return null

    return BrewingStandProperties(
        name = (brewingStand as? Nameable)?.customName?.string,
        biome = lookup.lookupBiomeId(world, brewingStand.pos),
        height = brewingStand.pos.y
    )
}
