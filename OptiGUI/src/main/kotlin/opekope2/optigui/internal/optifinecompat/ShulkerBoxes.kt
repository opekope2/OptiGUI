package opekope2.optigui.internal.optifinecompat

import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.ShulkerBoxBlockEntity
import net.minecraft.util.Nameable
import opekope2.filter.*
import opekope2.optifinecompat.FilterBuilder
import opekope2.optifinecompat.properties.ShulkerBoxProperties
import opekope2.optigui.resource.Resource
import opekope2.optigui.service.RegistryLookupService
import opekope2.optigui.service.getService
import opekope2.util.TexturePath
import opekope2.util.splitIgnoreEmpty

private const val CONTAINER = "shulker_box"
private val texture = TexturePath.SHULKER_BOX

internal fun createShulkerBoxFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != CONTAINER) return null
    val replacement = findReplacementTexture(resource) ?: return null

    val filter = FilterBuilder.build(resource) {
        setReplaceableTextures(texture)
        addGeneralFilters<ShulkerBoxProperties>()
        addFilterForProperty("colors", { it.splitIgnoreEmpty(*delimiters) }) { colors ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? ShulkerBoxProperties)?.color },
                FilterResult.Mismatch(),
                ContainingFilter(colors)
            )
        }
    }

    return FilterInfo(
        PostProcessorFilter(filter, replacement),
        setOf(texture)
    )
}

internal fun processShulkerBox(shulkerBox: BlockEntity): Any? {
    if (shulkerBox !is ShulkerBoxBlockEntity) return null
    val lookup = getService<RegistryLookupService>()

    val world = shulkerBox.world ?: return null

    return ShulkerBoxProperties(
        name = (shulkerBox as? Nameable)?.customName?.string,
        biome = lookup.lookupBiome(world, shulkerBox.pos),
        height = shulkerBox.pos.y,
        color = shulkerBox.color?.getName() // Because we need Color.name, and not Enum.name
    )
}
