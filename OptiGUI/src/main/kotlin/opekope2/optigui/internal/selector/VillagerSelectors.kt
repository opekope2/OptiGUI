package opekope2.optigui.internal.selector

import net.minecraft.util.Identifier
import opekope2.optigui.annotation.Selector
import opekope2.optigui.api.interaction.Interaction
import opekope2.optigui.api.selector.ISelector
import opekope2.optigui.filter.*
import opekope2.optigui.properties.IVillagerProperties
import opekope2.util.*


@Selector("villager.profession")
object VillagerProfessionSelector : ISelector {
    override fun createFilter(selector: String): IFilter<Interaction, *>? =
        selector.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map(::tryParseProfession) { throw RuntimeException("Invalid villager professions: ${joinNotFound(it)}") }
            ?.assertNotEmpty()
            ?.let { professions ->
                DisjunctionFilter(
                    professions.map { (profession, level) ->
                        val profFilter = PreProcessorFilter.nullGuarded<Interaction, Identifier, Unit>(
                            { (it.data as? IVillagerProperties)?.profession },
                            IFilter.Result.mismatch(),
                            EqualityFilter(profession)
                        )
                        val levelFilter = level?.toFilter()

                        if (levelFilter == null) profFilter
                        else ConjunctionFilter(
                            profFilter,
                            PreProcessorFilter.nullGuarded(
                                { (it.data as? IVillagerProperties)?.level },
                                IFilter.Result.mismatch(),
                                levelFilter
                            )
                        )
                    }
                )
            }

    private fun tryParseProfession(profession: String): Pair<Identifier, NumberOrRange?>? {
        return if ('@' in profession) {
            if (profession.count { it == '@' } > 2) return null
            val (profId, profLevel) = profession.split('@')
            val id = Identifier.tryParse(profId) ?: return null
            val level = NumberOrRange.tryParse(profLevel) ?: return null
            id to level
        } else {
            (Identifier.tryParse(profession) ?: return null) to null
        }
    }
}

@Selector("villager.type")
object VillagerTypeSelector : ISelector {
    override fun createFilter(selector: String): IFilter<Interaction, *>? =
        selector.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map(Identifier::tryParse) {
                throw RuntimeException("Invalid villager types: ${joinNotFound(it)}")
            }
            ?.assertNotEmpty()
            ?.let { types ->
                PreProcessorFilter.nullGuarded(
                    { (it.data as? IVillagerProperties)?.type },
                    IFilter.Result.mismatch(),
                    ContainingFilter(types)
                )
            }
}
