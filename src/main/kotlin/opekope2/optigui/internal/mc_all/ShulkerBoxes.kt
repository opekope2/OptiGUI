package opekope2.optigui.internal.mc_all

import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.ShulkerBoxBlockEntity
import net.minecraft.util.Nameable
import opekope2.filter.*
import opekope2.optigui.internal.properties.ShulkerBoxProperties
import opekope2.optigui.provider.IRegistryLookupProvider
import opekope2.optigui.provider.getProvider
import opekope2.optigui.resource.Resource
import opekope2.util.TexturePath
import opekope2.util.resolvePath
import opekope2.util.resolveResource
import opekope2.util.splitIgnoreEmpty
import java.io.File

private const val container = "shulker_box"
private val texture = TexturePath.SHULKER_BOX

internal fun createShulkerBoxFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != container) return null
    val resFolder = File(resource.id.path).parent.replace('\\', '/')
    val replacement = (resource.properties["texture"] as? String)?.let {
        resource.resourceManager.resolveResource(resolvePath(resFolder, it))
    } ?: return null

    val filters = createGeneralFilters(resource, container, texture)

    filters.addForProperty(resource, "colors", { it.splitIgnoreEmpty(*delimiters) }) { colors ->
        TransformationFilter(
            { (it.data as? ShulkerBoxProperties)?.color },
            ContainingFilter(colors)
        )
    }

    return FilterInfo(
        OverridingFilter(ConjunctionFilter(filters), replacement),
        setOf(texture)
    )
}

internal fun processShulkerBox(shulkerBox: BlockEntity): Any? {
    if (shulkerBox !is ShulkerBoxBlockEntity) return null
    val lookup = getProvider<IRegistryLookupProvider>()

    val world = shulkerBox.world ?: return null

    return ShulkerBoxProperties(
        container = container,
        texture = texture,
        name = (shulkerBox as? Nameable)?.customName?.string,
        biome = lookup.lookupBiome(world, shulkerBox.pos),
        height = shulkerBox.pos.y,
        color = shulkerBox.color?.getName() // Because we need Color.name, and not Enum.name
    )
}
