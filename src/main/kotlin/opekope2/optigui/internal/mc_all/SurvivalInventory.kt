package opekope2.optigui.internal.mc_all

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import opekope2.filter.*
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.properties.GeneralProperties
import opekope2.optigui.provider.IRegistryLookupProvider
import opekope2.optigui.provider.getProvider
import opekope2.optigui.resource.Resource
import opekope2.util.TexturePath
import opekope2.util.resolvePath
import opekope2.util.resolveResource
import java.io.File

private const val container = "inventory"
private val texture = TexturePath.INVENTORY

fun createSurvivalInventoryFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != container) return null
    val resFolder = File(resource.id.path).parent.replace('\\', '/')
    val replacement = (resource.properties["texture"] as? String)?.let {
        resource.resourceManager.resolveResource(resolvePath(resFolder, it))
    } ?: return null

    val filters = createGeneralFilters(resource, container, texture)

    return FilterInfo(
        OverridingFilter(
            TransformationFilter(
                ::processSurvivalInventory,
                NullSafeFilter(
                    skipOnNull = false,
                    failOnNull = true,
                    filter = ConjunctionFilter(filters)
                )
            ),
            replacement
        ),
        setOf(texture)
    )
}

private typealias SurvivalInventoryProperties = GeneralProperties

internal fun processSurvivalInventory(interaction: Interaction): Interaction? {
    val lookup = getProvider<IRegistryLookupProvider>()

    val mc = MinecraftClient.getInstance()
    val world = mc.world ?: return null
    val pos = BlockPos(mc.player?.pos ?: return null)

    return interaction.copy(
        data = SurvivalInventoryProperties(
            container = container,
            texture = texture,
            name = null,
            biome = lookup.lookupBiome(world, pos),
            height = pos.y
        )
    )
}
