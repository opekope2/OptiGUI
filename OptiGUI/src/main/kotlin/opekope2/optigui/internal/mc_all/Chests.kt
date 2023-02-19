package opekope2.optigui.internal.mc_all

import net.minecraft.block.entity.*
import net.minecraft.block.enums.ChestType
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.Nameable
import opekope2.filter.*
import opekope2.filter.FilterResult.Mismatch
import opekope2.optigui.internal.properties.ChestProperties
import opekope2.optigui.provider.RegistryLookup
import opekope2.optigui.provider.getProvider
import opekope2.optigui.resource.Resource
import opekope2.util.TexturePath
import opekope2.util.toBoolean
import java.time.LocalDateTime
import java.time.Month

private const val CONTAINER = "chest"
private val texture = TexturePath.GENERIC_54
private val chestTypeEnum = EnumProperty.of("type", ChestType::class.java)

internal fun createChestFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != CONTAINER) return null
    val replacement = findReplacementTexture(resource) ?: return null

    val filters = createGeneralFilters(resource, CONTAINER, texture)

    filters.addForProperty(resource, "large", { it.toBoolean() }) { large ->
        PreProcessorFilter.nullGuarded(
            { (it.data as? ChestProperties)?.large },
            Mismatch(),
            EqualityFilter(large)
        )
    }
    filters.addForProperty(resource, "trapped", { it.toBoolean() }) { trapped ->
        PreProcessorFilter.nullGuarded(
            { (it.data as? ChestProperties)?.trapped },
            Mismatch(),
            EqualityFilter(trapped)
        )
    }
    filters.addForProperty(resource, "christmas", { it.toBoolean() }) { christmas ->
        PreProcessorFilter.nullGuarded(
            { (it.data as? ChestProperties)?.christmas },
            Mismatch(),
            EqualityFilter(christmas)
        )
    }
    filters.addForProperty(resource, "ender", { it.toBoolean() }) { ender ->
        PreProcessorFilter.nullGuarded(
            { (it.data as? ChestProperties)?.ender },
            Mismatch(),
            EqualityFilter(ender)
        )
    }
    filters.addForProperty(resource, "_barrel", { it.toBoolean() }) { barrel ->
        PreProcessorFilter.nullGuarded(
            { (it.data as? ChestProperties)?.barrel },
            Mismatch(),
            EqualityFilter(barrel)
        )
    }

    return FilterInfo(
        PostProcessorFilter(ConjunctionFilter(filters), replacement),
        setOf(texture)
    )
}

internal fun processChest(chest: BlockEntity): Any? {
    if (chest !is LootableContainerBlockEntity && chest !is EnderChestBlockEntity) return null
    val lookup = getProvider<RegistryLookup>()

    val world = chest.world ?: return null
    val state = world.getBlockState(chest.pos)
    val type = state.entries[chestTypeEnum]

    return ChestProperties(
        container = CONTAINER,
        texture = texture,
        name = (chest as? Nameable)?.customName?.string,
        biome = lookup.lookupBiome(world, chest.pos),
        height = chest.pos.y,
        large = type != ChestType.SINGLE,
        trapped = chest is TrappedChestBlockEntity,
        christmas = isChristmas(),
        ender = chest is EnderChestBlockEntity,
        barrel = chest is BarrelBlockEntity
    )
}

private fun isChristmas(): Boolean = LocalDateTime.now().let { it.month == Month.DECEMBER && it.dayOfMonth in 24..26 }
