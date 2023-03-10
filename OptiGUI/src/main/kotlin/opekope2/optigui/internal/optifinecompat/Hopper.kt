package opekope2.optigui.internal.optifinecompat

import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.HopperBlockEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.vehicle.HopperMinecartEntity
import net.minecraft.util.Nameable
import opekope2.filter.ConjunctionFilter
import opekope2.filter.FilterInfo
import opekope2.filter.PostProcessorFilter
import opekope2.optifinecompat.properties.OptiFineProperties
import opekope2.optigui.resource.Resource
import opekope2.optigui.service.RegistryLookupService
import opekope2.optigui.service.getService
import opekope2.util.TexturePath

private const val CONTAINER = "hopper"
private val texture = TexturePath.HOPPER

fun createHopperFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != CONTAINER) return null
    val replacement = findReplacementTexture(resource) ?: return null

    val filters = createGeneralFilters(resource, CONTAINER, texture)

    return FilterInfo(
        PostProcessorFilter(ConjunctionFilter(filters), replacement),
        setOf(texture)
    )
}

private typealias HopperProperties = OptiFineProperties

internal fun processHopper(hopper: BlockEntity): Any? {
    if (hopper !is HopperBlockEntity) return null
    val lookup = getService<RegistryLookupService>()

    val world = hopper.world ?: return null

    return HopperProperties(
        container = CONTAINER,
        texture = texture,
        name = (hopper as? Nameable)?.customName?.string,
        biome = lookup.lookupBiome(world, hopper.pos),
        height = hopper.pos.y
    )
}

internal fun processHopperMinecart(minecart: Entity): Any? {
    if (minecart !is HopperMinecartEntity) return null
    val lookup = getService<RegistryLookupService>()

    val world = minecart.world ?: return null

    return HopperProperties(
        container = CONTAINER,
        texture = texture,
        name = minecart.customName?.string,
        biome = lookup.lookupBiome(world, minecart.blockPos),
        height = minecart.blockY
    )
}
