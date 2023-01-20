package opekope2.optigui.internal.mc_all

import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.HopperBlockEntity
import net.minecraft.util.Nameable
import opekope2.filter.ConjunctionFilter
import opekope2.filter.FilterInfo
import opekope2.filter.PostProcessorFilter
import opekope2.optigui.internal.properties.GeneralProperties
import opekope2.optigui.provider.IRegistryLookupProvider
import opekope2.optigui.provider.getProvider
import opekope2.optigui.resource.Resource
import opekope2.util.TexturePath
import opekope2.util.resolvePath
import opekope2.util.resolveResource
import java.io.File

private const val container = "hopper"
private val texture = TexturePath.HOPPER

fun createHopperFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != container) return null
    val resFolder = File(resource.id.path).parent.replace('\\', '/')
    val replacement = (resource.properties["texture"] as? String)?.let {
        resource.resourceManager.resolveResource(resolvePath(resFolder, it))
    } ?: return null

    val filters = createGeneralFilters(resource, container, texture)

    return FilterInfo(
        PostProcessorFilter(ConjunctionFilter(filters), replacement),
        setOf(texture)
    )
}

private typealias HopperProperties = GeneralProperties

internal fun processHopper(blockEntity: BlockEntity): Any? {
    if (blockEntity !is HopperBlockEntity) return null
    val lookup = getProvider<IRegistryLookupProvider>()

    val world = blockEntity.world ?: return null

    return HopperProperties(
        container = container,
        texture = texture,
        name = (blockEntity as? Nameable)?.customName?.string,
        biome = lookup.lookupBiome(world, blockEntity.pos),
        height = blockEntity.pos.y
    )
}
