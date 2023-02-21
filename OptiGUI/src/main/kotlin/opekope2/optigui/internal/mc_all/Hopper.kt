package opekope2.optigui.internal.mc_all

import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.HopperBlockEntity
import net.minecraft.util.Nameable
import opekope2.filter.ConjunctionFilter
import opekope2.filter.FilterInfo
import opekope2.filter.PostProcessorFilter
import opekope2.optigui.internal.properties.OptiFineProperties
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

internal fun processHopper(blockEntity: BlockEntity): Any? {
    if (blockEntity !is HopperBlockEntity) return null
    val lookup = getService<RegistryLookupService>()

    val world = blockEntity.world ?: return null

    return HopperProperties(
        container = CONTAINER,
        texture = texture,
        name = (blockEntity as? Nameable)?.customName?.string,
        biome = lookup.lookupBiome(world, blockEntity.pos),
        height = blockEntity.pos.y
    )
}
