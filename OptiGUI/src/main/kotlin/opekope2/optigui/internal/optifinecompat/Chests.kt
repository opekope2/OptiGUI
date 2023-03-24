package opekope2.optigui.internal.optifinecompat

import net.minecraft.block.entity.*
import net.minecraft.block.enums.ChestType
import net.minecraft.entity.Entity
import net.minecraft.entity.vehicle.ChestMinecartEntity
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.Nameable
import opekope2.filter.*
import opekope2.filter.FilterResult.Mismatch
import opekope2.optifinecompat.properties.ChestProperties
import opekope2.optigui.resource.Resource
import opekope2.optigui.service.RegistryLookupService
import opekope2.optigui.service.getService
import opekope2.util.TexturePath
import opekope2.util.isChristmas
import opekope2.util.toBoolean

private const val CONTAINER = "chest"
private val texture = TexturePath.GENERIC_54
private val chestTypeEnum = EnumProperty.of("type", ChestType::class.java)

internal fun createChestFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != CONTAINER) return null
    val replacement = findReplacementTexture(resource) ?: return null

    val filter = FilterBuilder.build(resource) {
        setReplaceableTextures(texture)
        addGeneralFilters<ChestProperties>()
        addFilterForProperty("large", { it.toBoolean() }) { large ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? ChestProperties)?.isLarge },
                Mismatch(),
                EqualityFilter(large)
            )
        }
        addFilterForProperty("trapped", { it.toBoolean() }) { trapped ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? ChestProperties)?.isTrapped },
                Mismatch(),
                EqualityFilter(trapped)
            )
        }
        addFilterForProperty("christmas", { it.toBoolean() }) { christmas ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? ChestProperties)?.isChristmas },
                Mismatch(),
                EqualityFilter(christmas)
            )
        }
        addFilterForProperty("ender", { it.toBoolean() }) { ender ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? ChestProperties)?.isEnder },
                Mismatch(),
                EqualityFilter(ender)
            )
        }
        addFilterForProperty("_barrel", { it.toBoolean() }) { barrel ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? ChestProperties)?.isBarrel },
                Mismatch(),
                EqualityFilter(barrel)
            )
        }
        addFilterForProperty("_minecart", { it.toBoolean() }) { minecart ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? ChestProperties)?.isMinecart },
                Mismatch(),
                EqualityFilter(minecart)
            )
        }
    }

    return FilterInfo(
        PostProcessorFilter(filter, replacement),
        setOf(texture)
    )
}

internal fun processChest(chest: BlockEntity): Any? {
    if (chest !is LootableContainerBlockEntity && chest !is EnderChestBlockEntity) return null
    val lookup = getService<RegistryLookupService>()

    val world = chest.world ?: return null
    val state = world.getBlockState(chest.pos)
    val type = state.entries[chestTypeEnum]

    return ChestProperties(
        name = (chest as? Nameable)?.customName?.string,
        biome = lookup.lookupBiome(world, chest.pos),
        height = chest.pos.y,
        isLarge = type != ChestType.SINGLE,
        isTrapped = chest is TrappedChestBlockEntity,
        isChristmas = isChristmas(),
        isEnder = chest is EnderChestBlockEntity,
        isBarrel = chest is BarrelBlockEntity,
        isMinecart = false
    )
}

internal fun processChestMinecart(minecart: Entity): Any? {
    if (minecart !is ChestMinecartEntity) return null
    val lookup = getService<RegistryLookupService>()

    val world = minecart.world ?: return null

    return ChestProperties(
        name = minecart.customName?.string,
        biome = lookup.lookupBiome(world, minecart.blockPos),
        height = minecart.blockY,
        isLarge = false,
        isTrapped = false,
        isChristmas = isChristmas(),
        isEnder = false,
        isBarrel = false,
        isMinecart = true
    )
}
