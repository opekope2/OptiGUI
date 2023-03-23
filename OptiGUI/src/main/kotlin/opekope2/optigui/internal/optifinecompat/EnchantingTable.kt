package opekope2.optigui.internal.optifinecompat

import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.EnchantingTableBlockEntity
import net.minecraft.util.Nameable
import opekope2.filter.FilterInfo
import opekope2.filter.PostProcessorFilter
import opekope2.optifinecompat.properties.OptiFineProperties
import opekope2.optigui.resource.Resource
import opekope2.optigui.service.RegistryLookupService
import opekope2.optigui.service.getService
import opekope2.util.TexturePath

private const val CONTAINER = "enchantment"
private val texture = TexturePath.ENCHANTING_TABLE

fun createEnchantingTableFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != CONTAINER) return null
    val replacement = findReplacementTexture(resource) ?: return null

    val filter = FilterBuilder.build(resource) {
        setReplaceableTextures(texture)
        addGeneralFilters<EnchantingTableProperties>()
    }

    return FilterInfo(
        PostProcessorFilter(filter, replacement),
        setOf(texture)
    )
}

private typealias EnchantingTableProperties = OptiFineProperties

internal fun processEnchantingTable(enchantingTable: BlockEntity): Any? {
    if (enchantingTable !is EnchantingTableBlockEntity) return null
    val lookup = getService<RegistryLookupService>()

    val world = enchantingTable.world ?: return null

    return EnchantingTableProperties(
        name = (enchantingTable as? Nameable)?.customName?.string,
        biome = lookup.lookupBiome(world, enchantingTable.pos),
        height = enchantingTable.pos.y
    )
}
