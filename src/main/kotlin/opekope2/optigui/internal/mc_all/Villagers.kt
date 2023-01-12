package opekope2.optigui.internal.mc_all

import net.minecraft.entity.Entity
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.entity.passive.WanderingTraderEntity
import net.minecraft.util.Identifier
import net.minecraft.util.Nameable
import opekope2.filter.*
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.properties.VillagerProperties
import opekope2.optigui.provider.IRegistryLookupProvider
import opekope2.optigui.provider.getProvider
import opekope2.optigui.resource.Resource
import opekope2.util.*
import java.io.File

private const val container = "villager"
private val texture = TexturePath.VILLAGER2
private val wanderingTraderProfession = Identifier(Identifier.DEFAULT_NAMESPACE, "_wandering_trader")

internal fun createVillagerFilter(resource: Resource): FilterInfo? {
    if (resource.properties["container"] != container) return null
    val resFolder = File(resource.id.path).parent.replace('\\', '/')
    val replacement = (resource.properties["texture"] as? String)?.let {
        resource.resourceManager.resolveResource(resolvePath(resFolder, it))
    } ?: return null

    val filters = createGeneralFilters(resource, container, texture)

    filters.addForProperty(
        resource,
        "professions",
        { it.splitIgnoreEmpty(*delimiters).mapNotNull(::parseProfession) }
    ) { professions ->
        val professionFilters: Collection<Filter<Interaction, Unit>> = professions.map { (profession, levels) ->
            ConjunctionFilter(
                TransformationFilter(
                    { (it.data as? VillagerProperties)?.profession },
                    NullableFilter(
                        skipOnNull = false,
                        failOnNull = true,
                        filter = EqualityFilter(profession)
                    )
                ),
                TransformationFilter(
                    { (it.data as? VillagerProperties)?.level },
                    NullableFilter(
                        skipOnNull = false,
                        failOnNull = true,
                        filter = DisjunctionFilter(levels.mapNotNull { it.toFilter() })
                    )
                )
            )
        }

        DisjunctionFilter(professionFilters)
    }

    return FilterInfo(
        OverridingFilter(ConjunctionFilter(filters), replacement),
        setOf(texture)
    )
}

internal fun processVillager(villager: Entity): Any? {
    if (villager !is VillagerEntity && villager !is WanderingTraderEntity) return null
    val lookup = getProvider<IRegistryLookupProvider>()

    val world = villager.world ?: return null
    val villagerData = (villager as? VillagerEntity)?.villagerData

    return VillagerProperties(
        container = container,
        texture = texture,
        name = (villager as? Nameable)?.customName?.string,
        biome = lookup.lookupBiome(world, villager.blockPos),
        height = villager.blockPos.y,
        profession = villagerData?.profession?.let { lookup.lookupVillagerProfessionId(it) }
            ?: wanderingTraderProfession,
        level = villagerData?.level
            ?: 1 // Wandering trader default level, because it can't level up
    )
}
