package opekope2.optigui.internal.mc_all

import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.EnchantingTableBlockEntity
import net.minecraft.util.Nameable
import opekope2.filter.ConjunctionFilter
import opekope2.filter.FilterInfo
import opekope2.filter.PostProcessorFilter
import opekope2.optigui.internal.properties.OptiFineProperties
import opekope2.optigui.provider.RegistryLookup
import opekope2.optigui.provider.getProvider
import opekope2.optigui.resource.Resource
import opekope2.util.TexturePath

private const val CONTAINER = "enchantment"
private val texture = TexturePath.ENCHANTING_TABLE

fun createEnchantingTableFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != CONTAINER) return null
    val replacement = findReplacementTexture(resource) ?: return null

    val filters = createGeneralFilters(resource, CONTAINER, texture)

    return FilterInfo(
        PostProcessorFilter(ConjunctionFilter(filters), replacement),
        setOf(texture)
    )
}

private typealias EnchantingTableProperties = OptiFineProperties

internal fun processEnchantingTable(enchantingTable: BlockEntity): Any? {
    if (enchantingTable !is EnchantingTableBlockEntity) return null
    val lookup = getProvider<RegistryLookup>()

    val world = enchantingTable.world ?: return null

    return EnchantingTableProperties(
        container = CONTAINER,
        texture = texture,
        name = (enchantingTable as? Nameable)?.customName?.string,
        biome = lookup.lookupBiome(world, enchantingTable.pos),
        height = enchantingTable.pos.y
    )
}
