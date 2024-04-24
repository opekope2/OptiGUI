package opekope2.optigui.internal.selector

import net.minecraft.entity.passive.*
import net.minecraft.util.DyeColor
import opekope2.optigui.filter.ContainingFilter
import opekope2.optigui.filter.EqualityFilter
import opekope2.optigui.filter.PreProcessorFilter
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.util.joinNotFound
import opekope2.optigui.selector.ISelector

internal class DonkeyChestSelector : ISelector {
    override fun createFilter(selector: String) = PreProcessorFilter.nullGuarded(
        ::hasDonkeyChest,
        "Check if donkey has chest",
        null,
        EqualityFilter(selector.toBooleanStrict())
    )

    private fun hasDonkeyChest(interaction: Interaction) =
        (interaction.data.entityOrRiddenEntity as? AbstractDonkeyEntity)?.hasChest()

    override fun getRawSelector(interaction: Interaction) = hasDonkeyChest(interaction)?.toString()
}

internal class HorseSaddleSelector : ISelector {
    override fun createFilter(selector: String) = PreProcessorFilter.nullGuarded(
        ::isHorseSaddled,
        "Check if horse is saddled",
        null,
        EqualityFilter(selector.toBooleanStrict())
    )

    private fun isHorseSaddled(interaction: Interaction) =
        (interaction.data.entityOrRiddenEntity as? AbstractHorseEntity)?.isSaddled

    override fun getRawSelector(interaction: Interaction): String? = isHorseSaddled(interaction)?.toString()
}

internal class HorseVariantSelector : AbstractListSelector<HorseColor>() {
    override fun parseSelector(selector: String) = HorseColor.entries.firstOrNull { it.name.lowercase() == selector }

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid horse variants: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<HorseColor>) = PreProcessorFilter.nullGuarded(
        ::transformInteraction,
        "Get horse variant",
        null,
        ContainingFilter(parsedSelectors)
    )

    override fun transformInteraction(interaction: Interaction) =
        (interaction.data.entityOrRiddenEntity as? HorseEntity)?.variant
}

internal class HorseMarkingSelector : AbstractListSelector<HorseMarking>() {
    override fun parseSelector(selector: String) = HorseMarking.entries.firstOrNull { it.name.lowercase() == selector }

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid horse markings: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<HorseMarking>) = PreProcessorFilter.nullGuarded(
        ::transformInteraction,
        "Get horse marking",
        null,
        ContainingFilter(parsedSelectors)
    )

    override fun transformInteraction(interaction: Interaction) =
        (interaction.data.entityOrRiddenEntity as? HorseEntity)?.marking
}

internal class LlamaCarpetColorSelector : AbstractListSelector<DyeColor>() {
    override fun parseSelector(selector: String) = DyeColor.byName(selector, null)

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid llama carpet colors: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<DyeColor>) = PreProcessorFilter.nullGuarded(
        ::getLlamaCarpetColor,
        "Get llama carpet color",
        null, // No carpet is mismatch, because at this point, a carpet is required
        ContainingFilter(parsedSelectors)
    )

    private fun getLlamaCarpetColor(interaction: Interaction) =
        (interaction.data.entityOrRiddenEntity as? LlamaEntity)?.carpetColor

    override fun transformInteraction(interaction: Interaction) = getLlamaCarpetColor(interaction)?.getName()
}

internal class LlamaVariantSelector : AbstractListSelector<LlamaEntity.Variant>() {
    override fun parseSelector(selector: String) =
        LlamaEntity.Variant.entries.firstOrNull { it.name.lowercase() == selector }

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid llama variants: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<LlamaEntity.Variant>) = PreProcessorFilter.nullGuarded(
        ::getLlamaVariant,
        "Get llama variant",
        null,
        ContainingFilter(parsedSelectors)
    )

    private fun getLlamaVariant(interaction: Interaction) =
        (interaction.data.entityOrRiddenEntity as? LlamaEntity)?.variant

    override fun transformInteraction(interaction: Interaction) = getLlamaVariant(interaction)?.asString()
}
