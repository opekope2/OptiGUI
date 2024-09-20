package opekope2.optigui.internal.selector

import opekope2.optigui.interaction.Interaction
import opekope2.optigui.util.getBiomeId

internal class LiteralPlayerNameSelector : LiteralNameSelector() {
    override fun getInteractionTargetCustomName(interaction: Interaction): String = interaction.data.player.name.string
}

internal class WildcardPlayerNameSelector(ignoreCase: Boolean) : WildcardNameSelector(ignoreCase) {
    override fun getInteractionTargetCustomName(interaction: Interaction): String = interaction.data.player.name.string
}

internal class RegexPlayerNameSelector(ignoreCase: Boolean) : RegexNameSelector(ignoreCase) {
    override fun getInteractionTargetCustomName(interaction: Interaction): String = interaction.data.player.name.string
}

internal class PlayerBiomeSelector : BiomeSelector() {
    override fun transformInteraction(interaction: Interaction) =
        interaction.data.world.getBiomeId(interaction.data.player.blockPos)
}

internal class PlayerHeightSelector : HeightSelector() {
    override fun transformInteraction(interaction: Interaction) = interaction.data.player.blockPos.y
}
