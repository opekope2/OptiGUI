package opekope2.optigui.internal.optifinecompat

import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.DispenserBlockEntity
import net.minecraft.block.entity.DropperBlockEntity
import net.minecraft.util.Nameable
import opekope2.filter.*
import opekope2.optifinecompat.properties.DispenserProperties
import opekope2.optigui.resource.Resource
import opekope2.optigui.service.RegistryLookupService
import opekope2.optigui.service.getService
import opekope2.util.TexturePath
import opekope2.util.splitIgnoreEmpty

private const val CONTAINER = "dispenser"
private val texture = TexturePath.DISPENSER

fun createDispenserFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != CONTAINER) return null
    val replacement = findReplacementTexture(resource) ?: return null

    val filters = createGeneralFilters(resource, CONTAINER, texture)

    filters.addForProperty(resource, "variants", { it.splitIgnoreEmpty(*delimiters) }) { variants ->
        PreProcessorFilter.nullGuarded(
            { (it.data as? DispenserProperties)?.variant },
            FilterResult.Mismatch(),
            ContainingFilter(variants)
        )
    }

    return FilterInfo(
        PostProcessorFilter(ConjunctionFilter(filters), replacement),
        setOf(texture)
    )
}

internal fun processDispenser(dispenser: BlockEntity): Any? {
    if (dispenser !is DispenserBlockEntity) return null
    val lookup = getService<RegistryLookupService>()

    val world = dispenser.world ?: return null

    val variant = when (dispenser) {
        is DropperBlockEntity -> "dropper"
        else -> "dispenser"
    }

    return DispenserProperties(
        container = CONTAINER,
        name = (dispenser as? Nameable)?.customName?.string,
        biome = lookup.lookupBiome(world, dispenser.pos),
        height = dispenser.pos.y,
        variant = variant
    )
}
