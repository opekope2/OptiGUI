package opekope2.optigui.internal.mc_all

import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.ShulkerBoxBlockEntity
import net.minecraft.util.Nameable
import opekope2.filter.*
import opekope2.optigui.internal.properties.ShulkerBoxProperties
import opekope2.optigui.provider.RegistryLookup
import opekope2.optigui.provider.getProvider
import opekope2.optigui.resource.Resource
import opekope2.util.TexturePath
import opekope2.util.splitIgnoreEmpty

private const val CONTAINER = "shulker_box"
private val texture = TexturePath.SHULKER_BOX

internal fun createShulkerBoxFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != CONTAINER) return null
    val replacement = findReplacementTexture(resource) ?: return null

    val filters = createGeneralFilters(resource, CONTAINER, texture)

    filters.addForProperty(resource, "colors", { it.splitIgnoreEmpty(*delimiters) }) { colors ->
        nullSafePreProcessorFilter(
            { (it.data as? ShulkerBoxProperties)?.color },
            FilterResult.Mismatch(),
            ContainingFilter(colors)
        )
    }

    return FilterInfo(
        PostProcessorFilter(ConjunctionFilter(filters), replacement),
        setOf(texture)
    )
}

internal fun processShulkerBox(shulkerBox: BlockEntity): Any? {
    if (shulkerBox !is ShulkerBoxBlockEntity) return null
    val lookup = getProvider<RegistryLookup>()

    val world = shulkerBox.world ?: return null

    return ShulkerBoxProperties(
        container = CONTAINER,
        texture = texture,
        name = (shulkerBox as? Nameable)?.customName?.string,
        biome = lookup.lookupBiome(world, shulkerBox.pos),
        height = shulkerBox.pos.y,
        color = shulkerBox.color?.getName() // Because we need Color.name, and not Enum.name
    )
}
