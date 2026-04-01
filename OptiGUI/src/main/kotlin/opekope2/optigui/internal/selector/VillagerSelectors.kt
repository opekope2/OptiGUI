package opekope2.optigui.internal.selector

import net.minecraft.world.entity.npc.Villager
import net.minecraft.resources.ResourceLocation
import opekope2.optigui.filter.*
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.util.joinNotFound
import opekope2.optigui.util.NumberOrRange
import kotlin.jvm.optionals.getOrNull

internal class VillagerProfessionSelector : AbstractListSelector<Pair<ResourceLocation, NumberOrRange?>>() {
    override fun parseSelector(selector: String): Pair<ResourceLocation, NumberOrRange?>? {
        val parts = selector.split('@')
        return when (parts.size) {
            1 -> (ResourceLocation.tryParse(parts[0]) ?: return null) to null
            2 -> {
                val (rawProfession, rawLevel) = parts

                val profId = ResourceLocation.tryParse(rawProfession) ?: return null
                val profLevel = NumberOrRange.tryParse(rawLevel) ?: return null

                profId to profLevel
            }

            else -> null
        }
    }

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid villager professions: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<Pair<ResourceLocation, NumberOrRange?>>) = DisjunctionFilter(
        parsedSelectors.map { (profession, level) ->
            val profFilter = PreProcessorFilter.nullGuarded(
                ::getVillagerProfession,
                "Get villager profession",
                null,
                EqualityFilter(profession)
            )
            val levelFilter = level?.toFilter()

            if (levelFilter == null) profFilter
            else ConjunctionFilter(
                profFilter,
                PreProcessorFilter.nullGuarded(
                    ::getVillagerLevel,
                    "Get villager level",
                    null,
                    levelFilter
                )
            )
        }
    )

    override fun transformInteraction(interaction: Interaction): String? {
        if (interaction.data.entity !is Villager) return null
        return "${getVillagerProfession(interaction)}@${getVillagerLevel(interaction)}"
    }

    private fun getVillagerProfession(interaction: Interaction) =
        (interaction.data.entity as? Villager)?.villagerData?.profession?.unwrapKey()?.getOrNull()?.location()

    private fun getVillagerLevel(interaction: Interaction) =
        (interaction.data.entity as? Villager)?.villagerData?.level
}

internal class VillagerTypeSelector : AbstractListSelector<ResourceLocation>() {
    override fun parseSelector(selector: String) = ResourceLocation.tryParse(selector)

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid villager types: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<ResourceLocation>) = PreProcessorFilter.nullGuarded(
        ::transformInteraction,
        "Get villager type",
        null,
        ContainingFilter(parsedSelectors)
    )

    override fun transformInteraction(interaction: Interaction) =
        (interaction.data.entity as? Villager)?.villagerData?.type?.unwrapKey()?.getOrNull()?.location()
}
