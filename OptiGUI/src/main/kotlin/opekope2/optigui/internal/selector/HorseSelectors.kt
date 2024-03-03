package opekope2.optigui.internal.selector

import net.minecraft.entity.passive.*
import net.minecraft.util.DyeColor
import opekope2.optigui.filter.ContainingFilter
import opekope2.optigui.filter.EqualityFilter
import opekope2.optigui.filter.IFilter.Result.Companion.mismatch
import opekope2.optigui.filter.PreProcessorFilter
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.util.joinNotFound
import opekope2.optigui.selector.ISelector

internal class DonkeyChestSelector : ISelector {
    override fun createFilter(selector: String) = PreProcessorFilter.nullGuarded(
        ::hasDonkeyChest,
        "Check if donkey has chest",
        mismatch(),
        EqualityFilter(selector.toBooleanStrict())
    )

    private fun hasDonkeyChest(interaction: Interaction) =
        (interaction.data.entityOrRiddenEntity as? AbstractDonkeyEntity)?.hasChest()

    override fun getRawSelector(interaction: Interaction) = hasDonkeyChest(interaction)?.toString()
}

internal class DonkeySaddleSelector : ISelector {
    override fun createFilter(selector: String) = PreProcessorFilter.nullGuarded(
        ::isDonkeySaddled,
        "Chest if horse is saddled",
        mismatch(),
        EqualityFilter(selector.toBooleanStrict())
    )

    private fun isDonkeySaddled(interaction: Interaction) =
        (interaction.data.entityOrRiddenEntity as? AbstractHorseEntity)?.isSaddled

    override fun getRawSelector(interaction: Interaction): String? = isDonkeySaddled(interaction)?.toString()
}

internal class HorseVariantSelector : AbstractListSelector<HorseColor>() {
    override fun parseSelector(selector: String) = HorseColor.entries.firstOrNull { it.name.lowercase() == selector }

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid horse variants: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<HorseColor>) = PreProcessorFilter.nullGuarded(
        ::transformInteraction,
        "Get horse variant",
        mismatch(),
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
        mismatch(),
        ContainingFilter(parsedSelectors)
    )

    override fun transformInteraction(interaction: Interaction) =
        (interaction.data.entityOrRiddenEntity as? HorseEntity)?.marking
}

internal class LlamaCarpetColorSelector : AbstractListSelector<DyeColor>() {
    override fun parseSelector(selector: String) = DyeColor.byName(selector, null)

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid llama colors: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<DyeColor>) = PreProcessorFilter.nullGuarded(
        ::transformInteraction,
        "Get llama carpet color",
        mismatch(), // No carpet is mismatch, because at this point, a carpet is required
        ContainingFilter(parsedSelectors)
    )

    override fun transformInteraction(interaction: Interaction) =
        (interaction.data.entityOrRiddenEntity as? LlamaEntity)?.carpetColor
}

internal class LlamaVariantSelector : AbstractListSelector<LlamaEntity.Variant>() {
    override fun parseSelector(selector: String) =
        LlamaEntity.Variant.entries.firstOrNull { it.name.lowercase() == selector }

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid llama variants: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<LlamaEntity.Variant>) = PreProcessorFilter.nullGuarded(
        ::transformInteraction,
        "Get llama variant",
        mismatch(),
        ContainingFilter(parsedSelectors)
    )

    override fun transformInteraction(interaction: Interaction) =
        (interaction.data.entityOrRiddenEntity as? LlamaEntity)?.variant
}
