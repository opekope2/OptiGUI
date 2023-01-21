package opekope2.optigui.internal.mc_all

import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.Nameable
import opekope2.filter.*
import opekope2.optigui.internal.properties.BeaconProperties
import opekope2.optigui.mixin.BeaconBlockEntityAccessorMixin
import opekope2.optigui.provider.RegistryLookup
import opekope2.optigui.provider.getProvider
import opekope2.optigui.resource.Resource
import opekope2.util.NumberOrRange
import opekope2.util.TexturePath
import opekope2.util.splitIgnoreEmpty

private const val CONTAINER = "beacon"
private val texture = TexturePath.BEACON

fun createBeaconFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != CONTAINER) return null
    val replacement = findReplacementTexture(resource) ?: return null

    val filters = createGeneralFilters(resource, CONTAINER, texture)

    filters.addForProperty(resource, "levels", { it.splitIgnoreEmpty(*delimiters) }) { levels ->
        val levelFilter = DisjunctionFilter(levels.mapNotNull { NumberOrRange.parse(it)?.toFilter() })

        Filter {
            levelFilter.evaluate((it.data as? BeaconProperties)?.level ?: return@Filter FilterResult.Mismatch())
        }
    }

    return FilterInfo(
        PostProcessorFilter(ConjunctionFilter(filters), replacement),
        setOf(texture)
    )
}

internal fun processBeacon(beacon: BlockEntity): Any? {
    if (beacon !is BeaconBlockEntity) return null
    val lookup = getProvider<RegistryLookup>()

    val world = beacon.world ?: return null

    return BeaconProperties(
        container = CONTAINER,
        texture = texture,
        name = (beacon as? Nameable)?.customName?.string,
        biome = lookup.lookupBiome(world, beacon.pos),
        height = beacon.pos.y,
        level = (beacon as BeaconBlockEntityAccessorMixin).level
    )
}
