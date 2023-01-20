package opekope2.optigui.internal.mc_all

import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.DispenserBlockEntity
import net.minecraft.block.entity.DropperBlockEntity
import net.minecraft.util.Nameable
import opekope2.filter.*
import opekope2.optigui.internal.properties.DispenserProperties
import opekope2.optigui.provider.IRegistryLookupProvider
import opekope2.optigui.provider.getProvider
import opekope2.optigui.resource.Resource
import opekope2.util.TexturePath
import opekope2.util.resolvePath
import opekope2.util.resolveResource
import opekope2.util.splitIgnoreEmpty
import java.io.File

private const val container = "dispenser"
private val texture = TexturePath.DISPENSER

fun createDispenserFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != container) return null
    val resFolder = File(resource.id.path).parent.replace('\\', '/')
    val replacement = (resource.properties["texture"] as? String)?.let {
        resource.resourceManager.resolveResource(resolvePath(resFolder, it))
    } ?: return null

    val filters = createGeneralFilters(resource, container, texture)

    filters.addForProperty(resource, "variants", { it.splitIgnoreEmpty(*delimiters) }) { variants ->
        val variantFilter = ContainingFilter(variants)

        Filter {
            variantFilter.evaluate((it.data as? DispenserProperties)?.variant ?: return@Filter FilterResult.Mismatch())
        }
    }

    return FilterInfo(
        PostProcessorFilter(ConjunctionFilter(filters), replacement),
        setOf(texture)
    )
}

internal fun processDispenser(dispenser: BlockEntity): Any? {
    if (dispenser !is DispenserBlockEntity) return null
    val lookup = getProvider<IRegistryLookupProvider>()

    val world = dispenser.world ?: return null

    val variant = when (dispenser) {
        is DropperBlockEntity -> "dropper"
        else -> "dispenser"
    }

    return DispenserProperties(
        container = container,
        texture = texture,
        name = (dispenser as? Nameable)?.customName?.string,
        biome = lookup.lookupBiome(world, dispenser.pos),
        height = dispenser.pos.y,
        variant = variant
    )
}
