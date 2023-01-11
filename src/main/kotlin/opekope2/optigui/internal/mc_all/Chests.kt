package opekope2.optigui.internal.mc_all

import net.minecraft.block.entity.*
import net.minecraft.block.enums.ChestType
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.Nameable
import opekope2.filter.*
import opekope2.optigui.internal.properties.ChestProperties
import opekope2.optigui.provider.IRegistryLookupProvider
import opekope2.optigui.provider.getProvider
import opekope2.optigui.resource.Resource
import opekope2.util.BuiltinTexturePath
import opekope2.util.resolvePath
import opekope2.util.resolveResource
import opekope2.util.toBoolean
import java.io.File
import java.time.LocalDateTime
import java.time.Month

private val chestTypeEnum = EnumProperty.of("type", ChestType::class.java)

internal fun createChestFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != "chest") return null
    val resFolder = File(resource.id.path).parent.replace('\\', '/')
    val replacement = (resource.properties["texture"] as? String)?.let {
        resource.resourceManager.resolveResource(resolvePath(resFolder, it))
    } ?: return null

    val filters = createGeneralFilters(resource, "chest", BuiltinTexturePath.CHEST)

    filters.addForProperty(resource, "large", { it.toBoolean() }) { large ->
        TransformationFilter(
            { (it.data as? ChestProperties)?.large },
            EqualityFilter(large)
        )
    }
    filters.addForProperty(resource, "trapped", { it.toBoolean() }) { trapped ->
        TransformationFilter(
            { (it.data as? ChestProperties)?.trapped },
            EqualityFilter(trapped)
        )
    }
    filters.addForProperty(resource, "christmas", { it.toBoolean() }) { christmas ->
        TransformationFilter(
            { (it.data as? ChestProperties)?.christmas },
            EqualityFilter(christmas)
        )
    }
    filters.addForProperty(resource, "ender", { it.toBoolean() }) { ender ->
        TransformationFilter(
            { (it.data as? ChestProperties)?.ender },
            EqualityFilter(ender)
        )
    }

    return FilterInfo(
        OverridingFilter(ConjunctionFilter(filters), replacement),
        setOf(BuiltinTexturePath.CHEST)
    )
}

internal fun processChest(chest: BlockEntity): Any? {
    if (chest !is LootableContainerBlockEntity && chest !is EnderChestBlockEntity) return null
    val lookup = getProvider<IRegistryLookupProvider>()

    val world = chest.world ?: return null
    val state = world.getBlockState(chest.pos)
    val type = state.entries[chestTypeEnum]

    return ChestProperties(
        container = "chest",
        texture = BuiltinTexturePath.CHEST,
        name = (chest as? Nameable)?.customName?.string,
        biome = lookup.lookupBiome(world, chest.pos),
        height = chest.pos.y,
        large = type != ChestType.SINGLE,
        trapped = chest is TrappedChestBlockEntity,
        christmas = isChristmas(),
        ender = chest is EnderChestBlockEntity
    )
}

private fun isChristmas(): Boolean = LocalDateTime.now().let { it.month == Month.DECEMBER && it.dayOfMonth in 24..26 }
