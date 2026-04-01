package opekope2.optigui.internal.selector

import net.minecraft.world.entity.animal.equine.*
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.block.WoolCarpetBlock
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
        (interaction.data.entityOrRiddenEntity as? AbstractChestedHorse)?.hasChest()

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
        (interaction.data.entityOrRiddenEntity as? AbstractHorse)?.isSaddled

    override fun getRawSelector(interaction: Interaction): String? = isHorseSaddled(interaction)?.toString()
}

internal class HorseVariantSelector : AbstractListSelector<Variant>() {
    override fun parseSelector(selector: String) = Variant.entries.firstOrNull { it.name.lowercase() == selector }

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid horse variants: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<Variant>) = PreProcessorFilter.nullGuarded(
        ::transformInteraction,
        "Get horse variant",
        null,
        ContainingFilter(parsedSelectors)
    )

    override fun transformInteraction(interaction: Interaction) =
        (interaction.data.entityOrRiddenEntity as? Horse)?.variant
}

internal class HorseMarkingSelector : AbstractListSelector<Markings>() {
    override fun parseSelector(selector: String) = Markings.entries.firstOrNull { it.name.lowercase() == selector }

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid horse markings: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<Markings>) = PreProcessorFilter.nullGuarded(
        ::transformInteraction,
        "Get horse marking",
        null,
        ContainingFilter(parsedSelectors)
    )

    override fun transformInteraction(interaction: Interaction) =
        (interaction.data.entityOrRiddenEntity as? Horse)?.markings
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
        (((interaction.data.entityOrRiddenEntity as? Llama)?.bodyArmorItem?.item as? BlockItem)?.block as? WoolCarpetBlock)?.color

    override fun transformInteraction(interaction: Interaction) = getLlamaCarpetColor(interaction)?.name
}

internal class LlamaVariantSelector : AbstractListSelector<Llama.Variant>() {
    override fun parseSelector(selector: String) =
        Llama.Variant.entries.firstOrNull { it.name.lowercase() == selector }

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid llama variants: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<Llama.Variant>) = PreProcessorFilter.nullGuarded(
        ::getLlamaVariant,
        "Get llama variant",
        null,
        ContainingFilter(parsedSelectors)
    )

    private fun getLlamaVariant(interaction: Interaction) =
        (interaction.data.entityOrRiddenEntity as? Llama)?.variant

    override fun transformInteraction(interaction: Interaction) = getLlamaVariant(interaction)?.serializedName
}
