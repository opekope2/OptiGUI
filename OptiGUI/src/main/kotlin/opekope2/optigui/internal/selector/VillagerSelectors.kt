package opekope2.optigui.internal.selector

import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import opekope2.optigui.filter.*
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.util.joinNotFound
import opekope2.optigui.util.NumberOrRange

internal class VillagerProfessionSelector : AbstractListSelector<Pair<Identifier, NumberOrRange?>>() {
    override fun parseSelector(selector: String): Pair<Identifier, NumberOrRange?>? {
        val parts = selector.split('@')
        return when (parts.size) {
            1 -> (Identifier.tryParse(parts[0]) ?: return null) to null
            2 -> {
                val (rawProfession, rawLevel) = parts

                val profId = Identifier.tryParse(rawProfession) ?: return null
                val profLevel = NumberOrRange.tryParse(rawLevel) ?: return null

                profId to profLevel
            }

            else -> null
        }
    }

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid villager professions: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<Pair<Identifier, NumberOrRange?>>) = DisjunctionFilter(
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
        if (interaction.data.entity !is VillagerEntity) return null
        return "${getVillagerProfession(interaction)}@${getVillagerLevel(interaction)}"
    }

    private fun getVillagerProfession(interaction: Interaction) =
        Registries.VILLAGER_PROFESSION.getId((interaction.data.entity as? VillagerEntity)?.villagerData?.profession)

    private fun getVillagerLevel(interaction: Interaction) =
        (interaction.data.entity as? VillagerEntity)?.villagerData?.level
}

internal class VillagerTypeSelector : AbstractListSelector<Identifier>() {
    override fun parseSelector(selector: String) = Identifier.tryParse(selector)

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid villager types: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<Identifier>) = PreProcessorFilter.nullGuarded(
        ::transformInteraction,
        "Get villager type",
        null,
        ContainingFilter(parsedSelectors)
    )

    override fun transformInteraction(interaction: Interaction) =
        Registries.VILLAGER_TYPE.getId((interaction.data.entity as? VillagerEntity)?.villagerData?.type)//TODO fix
}
