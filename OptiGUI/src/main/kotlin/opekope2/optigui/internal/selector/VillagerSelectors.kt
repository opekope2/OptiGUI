package opekope2.optigui.internal.selector

import net.minecraft.util.Identifier
import opekope2.optigui.annotation.Selector
import opekope2.optigui.api.interaction.Interaction
import opekope2.optigui.api.selector.ISelector
import opekope2.optigui.filter.*
import opekope2.optigui.properties.IVillagerProperties
import opekope2.util.*


@Selector("villager.profession")
class VillagerProfessionSelector : ISelector {
    override fun createFilter(selector: String): Filter<Interaction, *>? =
        selector.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map({ profession ->
                if ('@' in profession) {
                    if (profession.count { it == '@' } > 2) return@map null
                    val (profId, profLevel) = profession.split('@')
                    val id = Identifier.tryParse(profId) ?: return@map null
                    val level = NumberOrRange.tryParse(profLevel) ?: return@map null
                    id to level
                } else {
                    (Identifier.tryParse(profession) ?: return@map null) to null
                }
            }) { throw RuntimeException("Invalid professions: ${joinNotFound(it)}") }
            ?.assertNotEmpty()
            ?.let { professions ->
                DisjunctionFilter(
                    professions.map { (profession, level) ->
                        val profFilter = PreProcessorFilter.nullGuarded<Interaction, Identifier, Unit>(
                            { (it.data as? IVillagerProperties)?.profession },
                            FilterResult.mismatch(),
                            EqualityFilter(profession)
                        )
                        val levelFilter = level?.toFilter()

                        if (levelFilter == null) profFilter
                        else ConjunctionFilter(
                            profFilter,
                            PreProcessorFilter.nullGuarded(
                                { (it.data as? IVillagerProperties)?.level },
                                FilterResult.mismatch(),
                                levelFilter
                            )
                        )
                    }
                )
            }
}

@Selector("villager.type")
class VillagerTypeSelector : ISelector {
    override fun createFilter(selector: String): Filter<Interaction, *>? =
        selector.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map(Identifier::tryParse) {
                throw RuntimeException("Invalid villager types: ${joinNotFound(it)}")
            }
            ?.assertNotEmpty()
            ?.let { types ->
                PreProcessorFilter.nullGuarded(
                    { (it.data as? IVillagerProperties)?.type },
                    FilterResult.mismatch(),
                    ContainingFilter(types)
                )
            }
}
