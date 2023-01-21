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
        val largeFilter = createOptionalEqualityFilter(large)

        Filter {
            largeFilter.evaluate((it.data as? ChestProperties)?.large ?: return@Filter Mismatch())
        }
    }
    filters.addForProperty(resource, "trapped", { it.toBoolean() }) { trapped ->
        val trappedFilter = createOptionalEqualityFilter(trapped)

        Filter {
            trappedFilter.evaluate((it.data as? ChestProperties)?.trapped ?: return@Filter Mismatch())
        }
    }
    filters.addForProperty(resource, "christmas", { it.toBoolean() }) { christmas ->
        val christmasFilter = createOptionalEqualityFilter(christmas)

        Filter {
            christmasFilter.evaluate((it.data as? ChestProperties)?.christmas ?: return@Filter Mismatch())
        }
    }
    filters.addForProperty(resource, "ender", { it.toBoolean() }) { ender ->
        val enderFilter = createOptionalEqualityFilter(ender)

        Filter {
            enderFilter.evaluate((it.data as? ChestProperties)?.ender ?: return@Filter Mismatch())
        }
    }

    // _barrel needs to be opted in explicitly for compatibility
    if (resource.properties.containsKey("_barrel")) {
        filters.addForProperty(resource, "_barrel", { it.toBoolean() }) { barrel ->
            val barrelFilter = createOptionalEqualityFilter(barrel)

            Filter {
                barrelFilter.evaluate((it.data as? ChestProperties)?.barrel ?: return@Filter Mismatch())
            }
        }
    } else {
        val barrelFilter = EqualityFilter(false)

        filters += Filter {
            barrelFilter.evaluate((it.data as? ChestProperties)?.barrel ?: return@Filter Mismatch())
        }
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

private fun <T> createOptionalEqualityFilter(expectedValue: T?): Filter<T, Unit> =
    if (expectedValue == null) Filter { FilterResult.Match(Unit) }
    else EqualityFilter(expectedValue)
